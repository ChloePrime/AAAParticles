package mod.chloeprime.aaaparticles.api.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.chloeprime.aaaparticles.api.client.metadata.EffectFinalization;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * Metadata of an effek definition.
 * <p>
 * WARNING: Do not call its main constructor. Use {@link #CODEC} or {@link #DEFAULT} to obtain an instance.
 *
 * @param preload If true, this effek will be loaded during resource pack loading.
 *                <p>
 *                If false, this effek will be loaded when it is played the first time.
 *
 * @param size Intrinsic size of this effek. Used for referencing. Not used by AAAP itself.
 * @param finalization Finalization settings
 */
public record EffectMetadata(
        boolean preload,
        float size,
        EffectFinalization finalization
) {
    /**
     * Default metadata of effeks.
     * Used for effeks that has no corresponding metadata file.
     */
    public static final EffectMetadata DEFAULT = create(false, 1, EffectFinalization.DEFAULT);

    public static final Codec<EffectMetadata> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.BOOL.optionalFieldOf("preload", DEFAULT.preload()).forGetter(EffectMetadata::preload),
            Codec.FLOAT.optionalFieldOf("size", DEFAULT.size()).forGetter(EffectMetadata::size),
            EffectFinalization.CODEC.optionalFieldOf("finalization", EffectFinalization.DEFAULT).forGetter(EffectMetadata::finalization)
    ).apply(builder, EffectMetadata::create));

    /**
     * Get a valid finalization settings.
     *
     * @return finalization settings, empty if finalization settings are not set.
     */
    public Optional<EffectFinalization> getFinalizationSettings() {
        return Optional.ofNullable(finalization()).filter(EffectFinalization::isValid);
    }

    private static EffectMetadata create(boolean preload, float size, EffectFinalization finalization) {
        var lock = LockHolder.CONSTRUCTOR_LOCK.get();
        try {
            lock.increment();
            return new EffectMetadata(preload, size, finalization);
        } finally {
            lock.decrement();
        }
    }

    /**
     * Don't call this constructor.
     * Use deserialization instead of the main constructor to keep compatibility.
     *
     * @throws IllegalStateException when this constructor is ever called.
     */
    @ApiStatus.Internal
    public EffectMetadata {
        var lock = LockHolder.CONSTRUCTOR_LOCK.get();
        if (lock.intValue() == 0) {
            throw new IllegalStateException("This method is only available during deserialization");
        }
    }

    private static class LockHolder {
        private static final ThreadLocal<MutableInt> CONSTRUCTOR_LOCK = ThreadLocal.withInitial(MutableInt::new);
    }
}
