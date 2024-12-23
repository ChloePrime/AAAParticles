package mod.chloeprime.aaaparticles.client.registry;

import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerManager;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.client.loader.EffekAssetLoader;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 *
 * @author ChloePrime
 */
public class EffectRegistry {
    @Nullable
    public static EffectDefinition get(ResourceLocation id) {
        return EffekAssetLoader.get().get(id);
    }

    public static Collection<Map.Entry<ResourceLocation, EffectDefinition>> entries() {
        return EffekAssetLoader.get().entries();
    }

    public static void clearAllPlaying() {
        entries().stream()
                .map(Map.Entry::getValue)
                .flatMap(EffectDefinition::emitters)
                .forEach(ParticleEmitter::stop);

        entries().stream()
                .map(Map.Entry::getValue)
                .flatMap(EffectDefinition::managers)
                .forEach(EffekseerManager::stopAllEffects);
    }

    public static void forEach(BiConsumer<ResourceLocation, EffectDefinition> action) {
        EffekAssetLoader.get().forEach(action);
    }
}
