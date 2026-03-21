package mod.chloeprime.aaaparticles.client.registry;

import com.google.common.base.Suppliers;
import mod.chloeprime.aaaparticles.AAAParticles;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public final class LazyEffectDefinition implements Closeable {
    private Supplier<@Nullable EffectDefinition> rawGetter;
    private Supplier<@Nullable EffectDefinition> getter;
    private final AtomicBoolean isPresent = new AtomicBoolean();
    private boolean disposed;
    private long lastUsed = -1;

    public LazyEffectDefinition(Supplier<@Nullable EffectDefinition> rawGetter) {
        this.rawGetter = rawGetter;
        this.getter = Suppliers.memoize(rawGetter::get);
    }

    public boolean isPresent() {
        return !disposed && isPresent.get();
    }

    public Optional<EffectDefinition> lazyGet() {
        return isPresent() ? get() : Optional.empty();
    }

    public CompletableFuture<Optional<EffectDefinition>> load() {
        if (disposed) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        lastUsed = System.currentTimeMillis();
        if (isPresent()) {
            return CompletableFuture.completedFuture(get());
        }
        return CompletableFuture.completedFuture(Optional.ofNullable(getter.get()).map(def -> {
            AAAParticles.LOGGER.info("Loaded effek {}", EffectRegistry.getKey(this));
            isPresent.set(true);
            return def;
        }));
    }

    public long getLastUsed() {
        return disposed ? -1 : lastUsed;
    }

    public void unload() {
        if (disposed) {
            return;
        }
        AAAParticles.LOGGER.info("Unloaded effek {}", EffectRegistry.getKey(this));
        lazyGet().ifPresent(EffectDefinition::close);
        getter = Suppliers.memoize(rawGetter::get);
        isPresent.set(false);
    }

    public void close() {
        if (disposed) {
            return;
        }
        lazyGet().ifPresent(EffectDefinition::close);
        disposed = true;
        getter = null;
        rawGetter = null;
    }

    private Optional<EffectDefinition> get() {
        if (disposed) {
            return Optional.empty();
        }
        return Optional.ofNullable(getter.get());
    }
}
