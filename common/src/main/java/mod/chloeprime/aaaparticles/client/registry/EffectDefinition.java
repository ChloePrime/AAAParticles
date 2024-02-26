package mod.chloeprime.aaaparticles.client.registry;

import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerEffect;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerManager;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

/**
 * An effect wrapper with a registry name,
 * the effect instance is mutable.
 *
 * @author ChloePrime
 */
public class EffectDefinition {

    public ParticleEmitter play() {
        var emitter = manager.createParticle(getEffect());
        oneShotEmitters.add(emitter);
        return emitter;
    }


    public ParticleEmitter play(ResourceLocation emitterName) {
        var emitter = manager.createParticle(getEffect());
        var old = namedEmitters.put(emitterName, emitter);
        if (old != null) {
            old.stop();
        }
        return emitter;
    }

    public Stream<ParticleEmitter> emitters() {
        return Stream.concat(oneShotEmitters.stream(), namedEmitters.values().stream());
    }

    /**
     * @apiNote Do not keep reference of its return value.
     * Actual effect may be updated upon resource pack reloads.
     *
     * @return the effect that can be played directly.
     */
    public EffekseerEffect getEffect() {
        return effect;
    }

    public EffectDefinition setEffect(EffekseerEffect effect) {
        Objects.requireNonNull(effect);
        if (this.effect == effect) {
            return null;
        }
        // If this is not the first time of load.
        if (this.effect != null) {
            oneShotEmitters.forEach(ParticleEmitter::stop);
            oneShotEmitters.clear();
            namedEmitters.values().forEach(ParticleEmitter::stop);
            namedEmitters.clear();
            this.manager.close();
            this.manager = new EffekseerManager();
            this.effect.close();
        }
        this.effect = effect;
        initManager();
        return this;
    }

    private EffekseerEffect effect;
    private EffekseerManager manager = new EffekseerManager();
    private final Set<ParticleEmitter> oneShotEmitters = new LinkedHashSet<>();
    private final Map<ResourceLocation, ParticleEmitter> namedEmitters = new LinkedHashMap<>();
    private static final RandomGenerator RNG = new Random();
    private static final int GC_DELAY = 20;
    private final int magicLoadBalancer = (RNG.nextInt() >> 2) % GC_DELAY;
    private int gcTicks;

    public EffectDefinition() {
    }

    public void draw(int w, int h, float[] camera, float[] projection, float deltaFrames, float partialTicks) {
        manager.setViewport(w, h);
        manager.setCameraMatrix(camera);
        manager.setProjectionMatrix(projection);
        manager.update(deltaFrames);
        emitters().forEach(emitter -> emitter.runPreDrawCallbacks(partialTicks));
        manager.draw();

        gcTicks = (gcTicks + 1) % GC_DELAY;
        if (gcTicks == magicLoadBalancer) {
            oneShotEmitters.removeIf(emitter -> !emitter.exists());
            namedEmitters.values().removeIf(emitter -> !emitter.exists());
        }
    }

    private void initManager() {
        if (!manager.init(10000)) {
            throw new IllegalStateException("Failed to initialize EffekseerManager");
        }
        manager.setupWorkerThreads(2);
    }
}
