package mod.chloeprime.aaaparticles.api.client;

import com.google.common.base.Suppliers;
import com.sun.management.OperatingSystemMXBean;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerManager;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.client.loader.EffekAssetLoader;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    public static @Nullable EffectHolder get(Identifier id) {
        return Optional.ofNullable(EffekAssetLoader.get())
                .map(loader -> loader.get(id))
                .orElse(null);
    }

    /**
     * Load the effek with the given id. The returned future
     * only completes when the effek is loaded and not null,
     * otherwise it will complete exceptionally with an exception.
     *
     * @param id the wanted id of the returned effek.
     * @return The future of loading progress.
     * @since 2.1.0
     */
    public static CompletableFuture<@NotNull EffectDefinition> load(Identifier id) {
        var exception = Suppliers.memoize(() -> new RuntimeException("Effek loading failed."));
        var ret = new CompletableFuture<EffectDefinition>();
        var holder = get(id);
        if (holder != null) {
            holder.load().thenAccept(opt -> opt.ifPresentOrElse(ret::complete, () -> ret.completeExceptionally(exception.get())));
        } else {
            ret.completeExceptionally(exception.get());
        }
        return ret;
    }

    /**
     * Try to load the effek with the given id. The returned future
     * will always complete normally when the effek is loaded, even
     * when the load result is non-exist(null).
     *
     * @param id the wanted id of the returned effek.
     * @return The future of loading progress.
     * @since 2.1.0
     */
    public static CompletableFuture<Optional<EffectDefinition>> tryLoad(Identifier id) {
        return Optional.ofNullable(get(id))
                .map(EffectHolder::load)
                .orElseGet(() -> CompletableFuture.completedFuture(Optional.empty()));
    }

    /**
     * Get the id of the given effek holder.
     *
     * @param def the effek holder
     * @return the effek id.
     * @since 2.0.0 Effeks are lazily loaded.
     */
    public static @Nullable Identifier getKey(EffectHolder def) {
        return Optional.ofNullable(EffekAssetLoader.get())
                .map(loader -> loader.getKey(def))
                .orElse(null);
    }

    /**
     * Get the id of the given loaded effek.
     *
     * @param def the loaded effek definition
     * @since 2.0.0
     */
    public static @Nullable Identifier getKey(EffectDefinition def) {
        return def.getId();
    }

    /**
     * Get all effeks that has been scanned by the effek asset loader.
     */
    public static Collection<Map.Entry<Identifier, EffectHolder>> entries() {
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

    public static void forEach(BiConsumer<Identifier, EffectHolder> action) {
        Optional.ofNullable(EffekAssetLoader.get()).ifPresent(loader -> loader.forEach(action));
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
                .filter(holder -> !holder.getMetadata().preload())
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
