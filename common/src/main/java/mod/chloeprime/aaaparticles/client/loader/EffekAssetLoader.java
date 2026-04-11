package mod.chloeprime.aaaparticles.client.loader;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.api.client.EffectHolder;
import mod.chloeprime.aaaparticles.api.client.EffectMetadata;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerEffect;
import mod.chloeprime.aaaparticles.api.client.effekseer.TextureType;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import mod.chloeprime.aaaparticles.client.internal.LimitlessResourceLocationFactory;
import mod.chloeprime.aaaparticles.client.registry.EffectDefinition;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import mod.chloeprime.aaaparticles.client.render.RenderUtil;
import mod.chloeprime.aaaparticles.client.util.GlDebug;
import mod.chloeprime.aaaparticles.client.util.GlDebugIds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Loading effects from minecraft's resource system.
 *
 * @author ChloePrime
 */
public class EffekAssetLoader extends SimplePreparableReloadListener<EffekAssetLoader.Preparations> {
    private static EffekAssetLoader INSTANCE;
    public static EffekAssetLoader get() {
        return INSTANCE;
    }

    public @Nullable EffectHolder get(ResourceLocation id) {
        return loadedEffects.get(id);
    }

    public @Nullable ResourceLocation getKey(EffectHolder def) {
        return loadedEffects.inverse().get(def);
    }

    public Set<Map.Entry<ResourceLocation, EffectHolder>> entries() {
        return loadedEffects.entrySet();
    }

    public void forEach(BiConsumer<ResourceLocation, EffectHolder> action) {
        loadedEffects.forEach(action);
    }

    // Metadata Loading

    private static EffectMetadata loadMetadata(ResourceManager manager, ResourceLocation path) {
        return manager.getResource(path)
                .flatMap(res -> parseMetadata(res, path))
                .orElse(EffectMetadata.DEFAULT);
    }

    private static Optional<EffectMetadata> parseMetadata(Resource metaFile, ResourceLocation path) {
        JsonElement parsed;
        try (var input = metaFile.open()) {
            parsed = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            LOGGER.error("Failed to load effek metadata {}", path, ex);
            return Optional.empty();
        }
        var decoded = EffectMetadata.CODEC.decode(JsonOps.INSTANCE, parsed);
        decoded.error().ifPresent(error -> LOGGER.error("Error decoding effek metadata {}: {}", path, error.message()));
        return decoded.result().map(Pair::getFirst);
    }

    // Effek Loading

    private Supplier<Optional<EffekseerEffect>> loadEffect(ResourceManager manager, ResourceLocation name, Resource efkefc) {
        return () -> {
            GlDebug.pushDebugGroup(GlDebugIds.EFFEK_LOADING, () -> AAAParticles.LOG_PREFIX + " Loading effek %s".formatted(name));
            try (var input = efkefc.open()) {
                EffekseerEffect effect = new EffekseerEffect();
                boolean success = effect.load(input, 1);
                if (!success) {
                    LOGGER.error("Failed to load {}", name);
                    return Optional.empty();
                }

                try {
                    for (TextureType texType : TextureType.values()) {
                        int count = effect.textureCount(texType);
                        load(
                                manager,
                                name, count,
                                i -> effect.getTexturePath(i, texType),
                                (b, len, i) -> effect.loadTexture(b, len, i, texType)
                        );
                    }
                    load(manager, name, effect.modelCount(), effect::getModelPath, effect::loadModel);
                    load(manager, name, effect.curveCount(), effect::getCurvePath, effect::loadCurve);
                    load(manager, name, effect.materialCount(), effect::getMaterialPath, effect::loadMaterial);
                    return Optional.of(effect);
                } catch (FileNotFoundException ex) {
                    LOGGER.error("Failed to load {}", name, ex);
                    effect.close();
                    return Optional.empty();
                }
            } catch (IOException ex) {
                handleCheckedException(ex);
                return Optional.empty();
            } finally {
                GlDebug.popDebugGroup();
            }
        };
    }

    private static final BiFunction<String, String, ResourceLocation> UNVALIDATED_RES_LOC_FACTORY =
            ((LimitlessResourceLocationFactory) (Object) ResourceLocation.withDefaultNamespace("mole"))::aaa$createUninitialized;

    private void load(
            ResourceManager manager,
            ResourceLocation name, int count,
            IntFunction<String> pathGetter,
            TriConsumer<byte[]> loadMethod
    ) throws IOException {
        var modid = name.getNamespace();
        for (int i = 0; i < count; i++) {
            // example: "Texture/a.png" in 'sample.efkefc' from mod 'examplemod'
            String effekAssetPath = pathGetter.apply(i);
            String mcAssetPath = (Path.of("effeks", name.getPath()).getParent() + "/" + effekAssetPath)
                    .replace('\\', '/')
                    .replace("//", "/");
            String fallbackMcAssetPath = ("effeks/" + name.getPath() + "/" + effekAssetPath)
                    .replace('\\', '/')
                    .replace("//", "/");
            // example: "examplemod:effeks/Texture/a.png"

            var main = UNVALIDATED_RES_LOC_FACTORY.apply(modid, mcAssetPath);
            var fallback = UNVALIDATED_RES_LOC_FACTORY.apply(modid, fallbackMcAssetPath);
            // Load from disk.
            var resource = getResourceOrUseFallbackPath(manager, main, fallback)
                    .orElseThrow(() -> new FileNotFoundException("Failed to load %s or %s".formatted(main, fallback)));
            try (var input = resource.open()) {
                var data = input.readAllBytes();
                boolean success = loadMethod.accept(data, data.length, i);
                if (!success) {
                    String info = String.format("Failed to load effek data %s", effekAssetPath);
                    LOGGER.debug("\n{}\nmc asset path is \"{}\"", info, mcAssetPath);
                    throw new EffekLoadException(info);
                }
            }
        }
    }

    private static Optional<Resource> getResourceOrUseFallbackPath(
            ResourceManager manager,
            ResourceLocation path,
            ResourceLocation fallback
    ) {
        return manager.getResource(path).or(() -> manager.getResource(fallback));
    }

    // Preload Handling

    /**
     * Load all effeks that are marked as `preloading`
     */
    private void doPreloading() {
        loadedEffects.values().stream()
                .filter(holder -> holder.getMetadata().preload())
                .forEach(EffectHolder::load);
    }

    // Resource Load System

    protected static class Preparations {
        private final Map<ResourceLocation, EffectHolder> loadedEffects = new LinkedHashMap<>();
    }

    private void unloadAll() {
        loadedEffects.values().forEach(EffectHolder::close);
        loadedEffects.clear();
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    protected @NotNull Preparations prepare(ResourceManager manager, ProfilerFiller profilerFiller) {
        return null;
    }

    private static ResourceLocation createEffekName(ResourceLocation location) {
        var filePath = location.getPath();
        if (filePath.startsWith("effeks/")) {
            filePath = filePath.substring("effeks/".length());
        }
        if (filePath.endsWith(".efkefc") || filePath.endsWith(".efkpkg")) {
            filePath = filePath.substring(0, filePath.length() - ".efkefc".length());
        }
        return UNVALIDATED_RES_LOC_FACTORY.apply(location.getNamespace(), filePath);
    }

    @Override
    @SuppressWarnings("resource")
    protected void apply(Preparations prep_, ResourceManager manager, ProfilerFiller profilerFiller) {
        EffekRenderer.init();
        if (!NativePlatform.isRunningOnUnsupportedPlatform()) {
            unloadAll();
            RenderUtil.refreshBackgroundFrameBuffer();
            RenderUtil.runPixelStoreCodeHealthily(() -> {
                var prep = new Preparations();
                manager.listResources("effeks", rl -> rl.getPath().endsWith(".efkefc")).forEach((location, resource) -> {
                    var metaPath = new LimitlessResourceLocation(location.getNamespace(), location.getPath() + ".mcmeta");
                    var metadata = loadMetadata(manager, metaPath);
                    var name = createEffekName(location);
                    var loader = loadEffect(manager, name, resource);
                    var def = new EffectHolder(metadata, () -> loader.get()
                            .map(effect -> new EffectDefinition(metadata).setEffect(effect))
                            .orElse(null));
                    prep.loadedEffects.put(name, def);
                });
                loadedEffects.putAll(prep.loadedEffects);
            });
        }
        INSTANCE = this;
        doPreloading();
    }

    @FunctionalInterface
    private interface TriConsumer<T> {
        /**
         * Function object for methods like {@link EffekseerEffect#loadModel(byte[], int, int)}
         *
         * @param bytes  arg0
         * @param length arg2
         * @param index  arg2
         * @return success or fail
         * @see #load(ResourceManager, ResourceLocation, int, IntFunction, TriConsumer)
         */
        boolean accept(T bytes, int length, int index);
    }

    private static void handleCheckedException(Exception e) {
        throw new RuntimeException(e);
    }

    private static final Logger LOGGER = LogUtils.getLogger();

    private final BiMap<ResourceLocation, EffectHolder> loadedEffects = HashBiMap.create();
}

