package mod.chloeprime.aaaparticles.api.client.util;

import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.api.client.EffectDefinition;
import mod.chloeprime.aaaparticles.api.client.EffectDefinitionRouteResult;
import mod.chloeprime.aaaparticles.api.client.EffectMetadata;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.client.metadata.EffectRouting;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

/**
 * Effect definition that does nothing on play call.
 *
 * @since 2.3
 */
public class NullEffectDefinition extends EffectDefinition {
    /**
     * The ID for a special effect definition: {@code "c:dev/null"}.
     * This ID points to a special definition that does nothing on play call.
     * Can be routed to to disable certain particles on certain route settings.
     */
    public static final ResourceLocation ID = AAAParticles.loc("c", "dev/null");

    public NullEffectDefinition() {
        super(EffectMetadata.DEFAULT);
    }

    @Override
    public ParticleEmitter play() {
        return ParticleEmitter.dummy(ParticleEmitter.Type.WORLD);
    }

    @Override
    public ParticleEmitter play(ParticleEmitter.Type type) {
        return ParticleEmitter.dummy(type);
    }

    @Override
    public ParticleEmitter play(ResourceLocation emitterName) {
        return ParticleEmitter.dummy(ParticleEmitter.Type.WORLD);
    }

    @Override
    public ParticleEmitter play(ParticleEmitter.Type type, ResourceLocation emitterName) {
        return ParticleEmitter.dummy(type);
    }

    @Override
    public CompletableFuture<EffectDefinitionRouteResult> route(EffectRouting.QualityOptions quality, boolean lazy) {
        return CompletableFuture.completedFuture(new EffectDefinitionRouteResult(this, null, null));
    }
}
