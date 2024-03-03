package mod.chloeprime.aaaparticles.client.registry;

import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerEffect;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerManager;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import net.minecraft.resources.ResourceLocation;

import java.io.Closeable;
import java.util.*;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

/**
 * An effect wrapper with a registry name,
 * the effect instance is mutable.
 *
 * @author ChloePrime
 */
public class EffectDefinition implements Closeable {

    public ParticleEmitter play() {
        return play(ParticleEmitter.Type.WORLD);
    }

    public ParticleEmitter play(ParticleEmitter.Type type) {
        var emitter = getManager(type).createParticle(getEffect(), type);
        var collection = switch (type) {
            case WORLD -> oneShotEmitters;
            case FIRST_PERSON -> oneShotFpvEmitters;
        };
        collection.add(emitter);
        return emitter;
    }

    public ParticleEmitter play(ResourceLocation emitterName) {
        return play(emitterName, ParticleEmitter.Type.WORLD);
    }

    public ParticleEmitter play(ResourceLocation emitterName, ParticleEmitter.Type type) {
        var emitter = getManager(type).createParticle(getEffect(), type);
        var collection = switch (type) {
            case WORLD -> namedEmitters;
            case FIRST_PERSON -> namedFpvEmitters;
        };
        var old = collection.put(emitterName, emitter);
        if (old != null) {
            old.stop();
        }
        return emitter;
    }

    public EffekseerManager getManager(ParticleEmitter.Type type) {
        return switch (type) {
            case WORLD -> manager;
            case FIRST_PERSON -> fpvManager;
        };
    }

    public Stream<ParticleEmitter> emitters() {
        return emitterContainers().flatMap(Collection::stream);
    }

    public Stream<ParticleEmitter> emitters(ParticleEmitter.Type type) {
        return emitterContainers(type).flatMap(Collection::stream);
    }

    public Stream<Collection<ParticleEmitter>> emitterContainers() {
        return Stream.of(
                oneShotEmitters,
                oneShotFpvEmitters,
                namedEmitters.values(),
                namedFpvEmitters.values()
        );
    }

    public Stream<Collection<ParticleEmitter>> emitterContainers(ParticleEmitter.Type type) {
        return switch (type) {
            case WORLD -> Stream.of(oneShotEmitters, namedEmitters.values());
            case FIRST_PERSON -> Stream.of(oneShotFpvEmitters, namedFpvEmitters.values());
        };
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
            emitters().forEach(ParticleEmitter::stop);
            this.manager.close();
            this.manager = new EffekseerManager();
            this.fpvManager.close();
            this.fpvManager = new EffekseerManager();
            this.effect.close();
        }
        this.effect = effect;
        initManager();
        return this;
    }

    private EffekseerEffect effect;
    private EffekseerManager manager = new EffekseerManager();
    private EffekseerManager fpvManager = new EffekseerManager();
    private final Set<ParticleEmitter> oneShotEmitters = new LinkedHashSet<>();
    private final Map<ResourceLocation, ParticleEmitter> namedEmitters = new LinkedHashMap<>();
    private final Set<ParticleEmitter> oneShotFpvEmitters = new LinkedHashSet<>();
    private final Map<ResourceLocation, ParticleEmitter> namedFpvEmitters = new LinkedHashMap<>();
    private static final RandomGenerator RNG = new Random();
    private static final int GC_DELAY = 20;
    private final int magicLoadBalancer = (RNG.nextInt() >> 2) % GC_DELAY;
    private int gcTicks;

    public EffectDefinition() {
    }

    public void draw(ParticleEmitter.Type type, int w, int h, float[] camera, float[] projection, float deltaFrames, float partialTicks) {
        switch (type) {
            case WORLD -> draw(w, h, camera, projection, deltaFrames, partialTicks);
            case FIRST_PERSON -> drawFpv(w, h, camera, projection, deltaFrames, partialTicks);
        }
    }

    public void draw(int w, int h, float[] camera, float[] projection, float deltaFrames, float partialTicks) {
        draw(ParticleEmitter.Type.WORLD, manager, w, h, camera, projection, deltaFrames, partialTicks);
    }

    public void drawFpv(int w, int h, float[] camera, float[] projection, float deltaFrames, float partialTicks) {
        draw(ParticleEmitter.Type.FIRST_PERSON, fpvManager, w, h, camera, projection, deltaFrames, partialTicks);
    }

    protected void draw(ParticleEmitter.Type type, EffekseerManager manager, int w, int h, float[] camera, float[] projection, float deltaFrames, float partialTicks) {
        manager.setViewport(w, h);
        manager.setCameraMatrix(camera);
        manager.setProjectionMatrix(projection);
        manager.update(deltaFrames);
        emitters(type).forEach(emitter -> emitter.runPreDrawCallbacks(partialTicks));
        manager.draw();

        gcTicks = (gcTicks + 1) % GC_DELAY;
        if (gcTicks == magicLoadBalancer) {
            emitterContainers().forEach(container -> container.removeIf(emitter -> !emitter.exists()));
        }
    }

    private void initManager() {
        if (!manager.init(9000)) {
            throw new IllegalStateException("Failed to initialize EffekseerManager");
        }
        if (!fpvManager.init(1000)) {
            throw new IllegalStateException("Failed to initialize (fpv) EffekseerManager");
        }
        manager.setupWorkerThreads(2);
        fpvManager.setupWorkerThreads(1);
    }

    @Override
    public void close() {
        manager.close();
        fpvManager.close();
        effect.close();
    }
}
