package mod.chloeprime.aaaparticles.api.client.util;

import com.google.common.base.Suppliers;
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
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
    private final CompletableFuture<ParticleEmitter> whenEmitted = new CompletableFuture<>();
    private final Supplier<Queue<Consumer<ParticleEmitter>>> emitterActions = Suppliers.memoize(() -> new ArrayDeque<>(0));

    /**
     * Create a new instance of VanillaParticleProxy.
     *
     * @param effekId The Effek id.
     * @param level The client level instance.
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param dx Vanilla X speed.
     * @param dy Vanilla Y speed.
     * @param dz Vanilla Z speed.
     * @since 2.0.1 this constructor is public.
     */
    public VanillaParticleProxy(Identifier effekId, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
        super(level, x, y, z, dx, dy, dz, ClientPlatformMethods.get().getPlaceholderAtlasSprite26_1());
        this.effekId = effekId;
        this.lifetime = MAX_LIFE_TIME;
        spawn(effekId).thenAccept(opt -> opt.ifPresent(emitter -> {
            this.emitter = emitter;
            updatePosition(this.x, this.y, this.z);
            whenEmitted.complete(emitter);
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
     * Get the future of effek emitter of this particle.
     *
     * @return the future of the effek emitter of this particle.
     * @implNote Effeks are loaded synchronized, immediately during constructor in MC versions below 26.2.
     * @since 2.0.1
     */
    public CompletableFuture<ParticleEmitter> getEmitter() {
        return this.whenEmitted;
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
