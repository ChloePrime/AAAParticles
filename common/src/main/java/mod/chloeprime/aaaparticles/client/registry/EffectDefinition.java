package mod.chloeprime.aaaparticles.client.registry;

import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerEffect;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerManager;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import net.minecraft.client.Minecraft;
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
    public EffectDefinition() {
        for (ParticleEmitter.Type type : ParticleEmitter.Type.values()) {
            oneShotEmitters.put(type, new LinkedHashSet<>());
            namedEmitters.put(type, new LinkedHashMap<>());
        }
    }

    public ParticleEmitter play() {
        return play(ParticleEmitter.Type.WORLD);
    }

    public ParticleEmitter play(ResourceLocation emitterName) {
        return play(ParticleEmitter.Type.WORLD, emitterName);
    }

    public ParticleEmitter play(ParticleEmitter.Type type) {
        var emitter = getManager(type).createParticle(getEffect(), type);
        var collection = Objects.requireNonNull(oneShotEmitters.get(type));
        collection.add(emitter);
        return emitter;
    }

    public ParticleEmitter play(ParticleEmitter.Type type, ResourceLocation emitterName) {
        var emitter = getManager(type).createParticle(getEffect(), type);
        var collection = Objects.requireNonNull(namedEmitters.get(type));
        var old = collection.put(emitterName, emitter);
        if (old != null) {
            old.stop();
        }
        return emitter;
    }

    public EffekseerManager getManager(ParticleEmitter.Type type) {
        return Objects.requireNonNull(managers.get(type));
    }

    public Stream<ParticleEmitter> emitters() {
        return emitterContainers().flatMap(Collection::stream);
    }

    public Stream<ParticleEmitter> emitters(ParticleEmitter.Type type) {
        return emitterContainers(type).flatMap(Collection::stream);
    }

    public Stream<Collection<ParticleEmitter>> emitterContainers() {
        return Stream.concat(
                oneShotEmitters.values().stream(),
                namedEmitters.values().stream().map(Map::values)
        );
    }

    public Stream<Collection<ParticleEmitter>> emitterContainers(ParticleEmitter.Type type) {
        var oneshot = Objects.requireNonNull(oneShotEmitters.get(type));
        var named   = Objects.requireNonNull(namedEmitters.get(type)).values();
        return Stream.of(oneshot, named);
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
            managers().forEach(EffekseerManager::close);
            this.effect.close();
            this.managers.clear();
        }
        this.effect = effect;
        initManager();
        return this;
    }

    public Stream<EffekseerManager> managers() {
        return managers.values().stream();
    }

    private EffekseerEffect effect;
    private final EnumMap<ParticleEmitter.Type, EffekseerManager> managers = new EnumMap<>(ParticleEmitter.Type.class);
    private final EnumMap<ParticleEmitter.Type, Set<ParticleEmitter>> oneShotEmitters = new EnumMap<>(ParticleEmitter.Type.class);
    private final EnumMap<ParticleEmitter.Type, Map<ResourceLocation, ParticleEmitter>> namedEmitters = new EnumMap<>(ParticleEmitter.Type.class);
    private static final RandomGenerator RNG = new Random();
    private static final int GC_DELAY = 20;
    private final int magicLoadBalancer = Math.abs(RNG.nextInt() >>> 2) % GC_DELAY;
    private int gcTicks;

    public void draw(ParticleEmitter.Type type, int w, int h, float[] camera, float[] projection, float deltaFrames, float partialTicks) {
        var manager = Objects.requireNonNull(managers.get(type));
        manager.setViewport(w, h);
        manager.setCameraMatrix(camera);
        manager.setProjectionMatrix(projection);

        if (!Minecraft.getInstance().isPaused())
        {
            manager.startUpdate();
            manager.update(deltaFrames);
            manager.endUpdate();
        }

        emitters(type).forEach(emitter -> emitter.runPreDrawCallbacks(partialTicks));
        manager.draw();

        if (type == ParticleEmitter.Type.WORLD) {
            gcTicks = (gcTicks + 1) % GC_DELAY;
            if (gcTicks == magicLoadBalancer) {
                emitterContainers().forEach(container -> container.removeIf(emitter -> !emitter.exists()));
            }
        }
    }

    private void initManager() {
        for (ParticleEmitter.Type type : ParticleEmitter.Type.values()) {
            var old = this.managers.put(type, new EffekseerManager());
            Optional.ofNullable(old).ifPresent(EffekseerManager::close);
        }
        var worldManager = Objects.requireNonNull(managers.get(ParticleEmitter.Type.WORLD));
        var fpvMhManager = Objects.requireNonNull(managers.get(ParticleEmitter.Type.FIRST_PERSON_MAINHAND));
        var fpvOhManager = Objects.requireNonNull(managers.get(ParticleEmitter.Type.FIRST_PERSON_OFFHAND));
        if (!worldManager.init(9000)) {
            throw new IllegalStateException("Failed to initialize EffekseerManager");
        }
        if (!fpvMhManager.init(500)) {
            throw new IllegalStateException("Failed to initialize (fpv mainhand) EffekseerManager");
        }
        if (!fpvOhManager.init(500)) {
            throw new IllegalStateException("Failed to initialize (fpv offhand) EffekseerManager");
        }
        worldManager.setupWorkerThreads(2);
        fpvMhManager.setupWorkerThreads(1);
        fpvOhManager.setupWorkerThreads(1);
    }

    @Override
    public void close() {
        managers.values().forEach(EffekseerManager::close);
        effect.close();
    }
}
