package mod.chloeprime.aaaparticles.api.client.util;

import mod.chloeprime.aaaparticles.api.client.EffectDefinition;
import mod.chloeprime.aaaparticles.api.client.EffectHolder;
import mod.chloeprime.aaaparticles.api.client.EffectRegistry;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.client.ClientPlatformMethods;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A vanilla particle that plays and holds an Effek emitter.
 */
@ApiStatus.Experimental
@SuppressWarnings("unused")
public class VanillaParticleProxy extends SingleQuadParticle {
    private static final int MAX_LIFE_TIME = 20 * 60 * 30;
    private static final int MAX_WAIT_TIME = 20 * 5;
    private int age;
    private final Identifier effekId;
    private @Nullable ParticleEmitter emitter;

    protected VanillaParticleProxy(Identifier effekId, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
        super(level, x, y, z, dx, dy, dz, ClientPlatformMethods.get().getPlaceholderAtlasSprite26_1());
        this.effekId = effekId;
        this.lifetime = MAX_LIFE_TIME;
        spawn(effekId).thenAccept(opt -> opt.ifPresent(emitter -> {
            this.emitter = emitter;
            updatePosition(this.x, this.y, this.z);
        }));
    }

    /**
     * Get the effek id of this particle.
     *
     * @return the effek id of this particle.
     */
    public Identifier getEffekId() {
        return effekId;
    }

    /**
     * Override tick and call this if you want to make this emitter move.
     */
    public void move() {
        super.tick();
    }

    private void updatePosition(double x, double y, double z) {
        var emitter = this.emitter;
        if (emitter != null) {
            emitter.setPosition((float) x, (float) y, (float) z);
        }
    }

    @Override
    public void tick() {
        if (removed) {
            return;
        }
        var emitter = this.emitter;
        if (this.age > this.lifetime) {
            remove();
        } else if (emitter == null && this.age > MAX_WAIT_TIME) {
            remove();
        } else if (emitter != null && !emitter.exists()) {
            remove();
        } else {
            super.age = this.age++;
        }
    }

    @Override
    public @NonNull ParticleRenderType getGroup() {
        return ParticleRenderType.SINGLE_QUADS;
    }

    @Override
    protected @NonNull Layer getLayer() {
        return Layer.OPAQUE;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void extract(QuadParticleRenderState particleTypeRenderState, Camera camera, float partialTicks) {
        double x = Mth.lerp(partialTicks, this.xo, this.x);
        double y = Mth.lerp(partialTicks, this.yo, this.y);
        double z = Mth.lerp(partialTicks, this.zo, this.z);
        updatePosition(x, y, z);
    }

    private static CompletableFuture<Optional<ParticleEmitter>> spawn(Identifier effekId) {
        var loaded = Optional.ofNullable(EffectRegistry.get(effekId)).map(EffectHolder::load).orElse(null);
        if (loaded == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return loaded.thenApply(opt -> opt.map(EffectDefinition::play));
    }
}
