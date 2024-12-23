package mod.chloeprime.aaaparticles.client.loader;

import com.mojang.logging.LogUtils;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerEffect;
import mod.chloeprime.aaaparticles.api.client.effekseer.TextureType;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import mod.chloeprime.aaaparticles.client.registry.EffectDefinition;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import mod.chloeprime.aaaparticles.client.render.RenderUtil;
import mod.chloeprime.aaaparticles.common.util.LimitlessResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;

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

    @Nullable
    public EffectDefinition get(ResourceLocation id) {
        return loadedEffects.get(id);
    }

    public Set<Map.Entry<ResourceLocation, EffectDefinition>> entries() {
        return loadedEffects.entrySet();
    }

    public void forEach(BiConsumer<ResourceLocation, EffectDefinition> action) {
        loadedEffects.forEach(action);
    }

    private Optional<EffekseerEffect> loadEffect(ResourceManager manager, ResourceLocation name, Resource efkefc) {
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
        }
    }

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

            var main = new LimitlessResourceLocation(modid, mcAssetPath);
            var fallback = new LimitlessResourceLocation(modid, fallbackMcAssetPath);
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

    protected static class Preparations {
        private final Map<ResourceLocation, EffectDefinition> loadedEffects = new LinkedHashMap<>();
    }

    private void unloadAll() {
        loadedEffects.values().forEach(EffectDefinition::close);
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
        return new ResourceLocation(location.getNamespace(), filePath);
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
                    var name = createEffekName(location);
                    loadEffect(manager, name, resource).ifPresent(effect -> prep.loadedEffects.put(name, new EffectDefinition().setEffect(effect)));
                });
                loadedEffects.putAll(prep.loadedEffects);
            });
        }
        INSTANCE = this;
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

    private final Map<ResourceLocation, EffectDefinition> loadedEffects = new LinkedHashMap<>();
}

