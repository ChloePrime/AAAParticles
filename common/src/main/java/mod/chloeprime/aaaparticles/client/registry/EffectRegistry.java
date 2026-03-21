package mod.chloeprime.aaaparticles.client.registry;

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
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 *
 * @author ChloePrime
 */
public class EffectRegistry {
    public static @Nullable LazyEffectDefinition get(ResourceLocation id) {
        return EffekAssetLoader.get().get(id);
    }

    public static @Nullable ResourceLocation getKey(LazyEffectDefinition def) {
        return EffekAssetLoader.get().getKey(def);
    }

    public static Collection<Map.Entry<ResourceLocation, LazyEffectDefinition>> entries() {
        return EffekAssetLoader.get().entries();
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
                .filter(LazyEffectDefinition::isPresent)
                .sorted(Comparator.comparing(LazyEffectDefinition::getLastUsed))
                .limit(1)
                .forEach(LazyEffectDefinition::unload);
    }

    public static void clearAllPlaying() {
        entries().stream()
                .map(Map.Entry::getValue)
                .map(LazyEffectDefinition::lazyGet)
                .flatMap(Optional::stream)
                .flatMap(EffectDefinition::emitters)
                .forEach(ParticleEmitter::stop);

        entries().stream()
                .map(Map.Entry::getValue)
                .map(LazyEffectDefinition::lazyGet)
                .flatMap(Optional::stream)
                .flatMap(EffectDefinition::managers)
                .forEach(EffekseerManager::stopAllEffects);
    }

    public static void forEach(BiConsumer<ResourceLocation, LazyEffectDefinition> action) {
        EffekAssetLoader.get().forEach(action);
    }
}
