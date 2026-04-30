package mod.chloeprime.aaaparticles.api.client;

import com.google.common.base.Suppliers;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.render.RenderUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * A lazy holder that defers the loading of effeks.
 *
 * @since 2.0.0
 * @author ChloePrime
 */
public final class EffectHolder implements Closeable {
    private final EffectMetadata metadata;
    private Supplier<@Nullable EffectDefinition> rawGetter;
    private Supplier<@Nullable EffectDefinition> getter;
    private final AtomicBoolean isPresent = new AtomicBoolean();
    private boolean disposed;
    private long lastUsed = -1;

    public EffectHolder(
            @NotNull EffectMetadata metadata,
            Supplier<@Nullable EffectDefinition> rawGetter
    ) {
        this.metadata = Objects.requireNonNull(metadata);
        this.rawGetter = () -> RenderUtil.supplyEffekLoadCodeHealthily(rawGetter);
        this.getter = Suppliers.memoize(this.rawGetter::get);
    }

    /**
     * Get the metadata of this effek.
     *
     * @return metadata of this effek.
     */
    public @NotNull EffectMetadata getMetadata() {
        return metadata;
    }

    /**
     * Get whether the effek under this holder has been loaded.
     *
     * @return whether this holder is already resolved (loaded).
     */
    public boolean isPresent() {
        return !disposed && isPresent.get();
    }

    /**
     * Get the loaded {@link EffectDefinition} if this effect has been loaded.
     * This operation WILL NOT load the effek and its resources from memory.
     *
     * @return the loaded {@link EffectDefinition}, or {@link Optional#empty()} if this effect has not been loaded or does not exist.
     */
    public Optional<EffectDefinition> lazyGet() {
        return isPresent() ? get() : Optional.empty();
    }

    /**
     * Load and get the holder's corresponding {@link EffectDefinition}.
     * This operation WILL load the effek and its resources from memory.
     *
     * @return the loaded {@link EffectDefinition}, or {@link Optional#empty()} if this effect does not exist.
     * @see EffectRegistry#load
     * @see EffectRegistry#tryLoad
     */
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

    /**
     * Get the last timestamp when this effect was loaded, in milliseconds.
     *
     * @return the last timestamp when this effect was loaded, in milliseconds
     */
    public long getLastUsed() {
        return disposed ? -1 : lastUsed;
    }

    /**
     * Unload the loaded effek from memory.
     * This holder can be loaded again later after this method has been called.
     */
    public void unload() {
        if (disposed) {
            return;
        }
        AAAParticles.LOGGER.info("Unloaded effek {}", EffectRegistry.getKey(this));
        lazyGet().ifPresent(EffectDefinition::close);
        getter = Suppliers.memoize(rawGetter::get);
        isPresent.set(false);
    }

    /**
     * Dispose this holder and its loaded effek from memory.
     * Call to this method will make this holder no longer loadable anymore.
     */
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
