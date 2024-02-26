package mod.chloeprime.aaaparticles.client.loader;

import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerEffect;
import mod.chloeprime.aaaparticles.api.client.effekseer.TextureType;
import mod.chloeprime.aaaparticles.client.registry.EffectDefinition;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import mod.chloeprime.aaaparticles.common.util.LimitlessResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
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

    /**
     * @param name the minecraft asset path of .efkefc files.
     * @return the loaded effect.
     */
    private EffekseerEffect loadEffect(ResourceManager manager, ResourceLocation name) {
        String modid = name.getNamespace();
        String path = "effeks/" + name.getPath() + ".efkefc";
        ResourceLocation assetLocation = new ResourceLocation(modid, path);

        try (Resource efkefc = manager.getResource(assetLocation)) {
            EffekseerEffect effect = new EffekseerEffect();
            boolean success = effect.load(efkefc.getInputStream(), 1);
            if (!success) {
                throw new EffekLoadException("Failed to load " + assetLocation);
            }

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
            return effect;
        } catch (IOException ex) {
            handleCheckedException(ex);
            return null;
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
            String mcAssetPath = ("effeks/" + effekAssetPath)
                    .replace('\\', '/')
                    .replace("//", "/");
            String fallbackMcAssetPath = ("effeks/" + name.getPath() + "/" + effekAssetPath)
                    .replace('\\', '/')
                    .replace("//", "/");
            // example: "examplemod:effeks/Texture/a.png"

            var main = new LimitlessResourceLocation(modid, mcAssetPath);
            var fallback = new LimitlessResourceLocation(modid, fallbackMcAssetPath);
            // Load from disk.
            try (var resource = getResourceOrUseFallbackPath(manager, main, fallback)) {
                byte[] bytes = IOUtils.toByteArray(resource.getInputStream());
                boolean success = loadMethod.accept(bytes, bytes.length, i);
                if (!success) {
                    String info = String.format("Failed to load effek data %s", effekAssetPath);
                    LOGGER.debug(String.format("\n%s\nmc asset path is \"%s\"", info, mcAssetPath));
                    throw new EffekLoadException(info);
                }
            }
        }
    }

    private static Resource getResourceOrUseFallbackPath(
            ResourceManager manager,
            ResourceLocation path,
            ResourceLocation fallback
    ) throws IOException {
        try {
            return manager.getResource(path);
        } catch (FileNotFoundException ignored) {
            return manager.getResource(fallback);
        }
    }

    protected static class Preparations {
        private final Map<ResourceLocation, EffectDefinition> loadedEffects = new LinkedHashMap<>();
    }

    private void unloadAll() {
        loadedEffects.forEach((id, definition) -> definition.close());
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
        var prep = new Preparations();
        for (var effek : manager.listResources("effeks", s -> s.endsWith(".efkefc"))) {
            var name = createEffekName(effek);
            prep.loadedEffects.put(name, new EffectDefinition().setEffect(loadEffect(manager, name)));
        }
        unloadAll();
        loadedEffects.putAll(prep.loadedEffects);
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
    private static final Logger LOGGER = LogManager.getLogger(EffekAssetLoader.class.getSimpleName());

    private final Map<ResourceLocation, EffectDefinition> loadedEffects = new LinkedHashMap<>();
}

