package mod.chloeprime.aaaparticles.api.client.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;

/**
 * Finalization control of effeks.
 * Only affects {@link ParticleEmitterInfo}s that are bound to entities.
 *
 * @param trigger the trigger ID when the bound entity is not exist anymore
 * @param delay delay between the bound entity's disappearance and the emitter's destruction
 * @since 2.1.0
 */
public record EffectFinalization(
        int trigger,
        int delay
) {
    /**
     * Default finalization settings.
     * <br>
     * This is an invalid value, using this will not take any effect.
     */
    public static final EffectFinalization DEFAULT = new EffectFinalization(-1, 0);

    public static final Codec<EffectFinalization> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("trigger").forGetter(EffectFinalization::trigger),
            Codec.INT.fieldOf("delay").forGetter(EffectFinalization::delay)
    ).apply(builder, EffectFinalization::new));

    /**
     * Check whether this finalization settings is valid.
     *
     * @return true when this finalization settings is valid
     */
    public boolean isValid() {
        return trigger >= 0 && delay > 0;
    }
}
