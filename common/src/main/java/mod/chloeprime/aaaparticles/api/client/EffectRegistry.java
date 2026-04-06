package mod.chloeprime.aaaparticles.api.client;

import com.sun.management.OperatingSystemMXBean;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerManager;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.client.loader.EffekAssetLoader;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * AAAParticles Effect Registry.
 *
 * @since 1.0.0
 * @author ChloePrime
 */
public class EffectRegistry {
    /**
     * Get the effek holder with the given id.
     *
     * @param id the wanted id of the returned effek.
     * @return the effek holder.
     * @since 2.0.0 Effeks are lazily loaded.
     */
    public static @Nullable EffectHolder get(ResourceLocation id) {
        return EffekAssetLoader.get().get(id);
    }

    /**
     * Get the id of the given effek holder.
     *
     * @param def the effek holder
     * @return the effek id.
     * @since 2.0.0 Effeks are lazily loaded.
     */
    public static @Nullable ResourceLocation getKey(EffectHolder def) {
        return EffekAssetLoader.get().getKey(def);
    }

    /**
     * Get the id of the given loaded effek.
     *
     * @param def the loaded effek definition
     * @since 2.0.0
     */
    public static @Nullable ResourceLocation getKey(EffectDefinition def) {
        return def.getId();
    }

    /**
     * Get all effeks that has been scanned by the effek asset loader.
     */
    public static Collection<Map.Entry<ResourceLocation, EffectHolder>> entries() {
        return Optional.ofNullable(EffekAssetLoader.get())
                .map(EffekAssetLoader::entries)
                .orElse(Collections.emptySet());
    }

    /**
     * Clear all playing effek emitters.
     */
    public static void clearAllPlaying() {
        entries().stream()
                .map(Map.Entry::getValue)
                .map(EffectHolder::lazyGet)
                .flatMap(Optional::stream)
                .flatMap(EffectDefinition::emitters)
                .forEach(ParticleEmitter::stop);

        entries().stream()
                .map(Map.Entry::getValue)
                .map(EffectHolder::lazyGet)
                .flatMap(Optional::stream)
                .flatMap(EffectDefinition::managers)
                .forEach(EffekseerManager::stopAllEffects);
    }

    public static void forEach(BiConsumer<ResourceLocation, EffectHolder> action) {
        EffekAssetLoader.get().forEach(action);
    }

    /**
     * Release less recently used effeks when system is under high memory usage.
     * This mod is called automatically regularly.
     */
    public static void gc() {
        boolean lowFreeMem;
        var runtime = Runtime.getRuntime();
        if (ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean os) {
            // 系统剩余内存小于为游戏分配内存的 1 / 4 时视作内存不足
            lowFreeMem = os.getFreeMemorySize() / (double) runtime.maxMemory() <= 0.25;
        } else {
            // 备用逻辑，一般不应该走这里
            var bean = ManagementFactory.getMemoryMXBean();
            var heap = bean.getHeapMemoryUsage();
            var offh = bean.getNonHeapMemoryUsage();
            lowFreeMem = (heap.getUsed() + offh.getUsed()) / (double) (heap.getMax() + offh.getMax()) <= 0.125;
        }
        if (!lowFreeMem) {
            return;
        }
        entries().stream()
                .map(Map.Entry::getValue)
                .filter(EffectHolder::isPresent)
                .sorted(Comparator.comparing(EffectHolder::getLastUsed))
                .limit(1)
                .forEach(EffectHolder::unload);
    }

    static {
        if (Platform.getEnvironment() == Env.CLIENT) {
            ClientTickEvent.CLIENT_LEVEL_POST.register(level -> {
                if (level.getGameTime() % 5 == 4) {
                    gc();
                }
            });
        }
    }
}
