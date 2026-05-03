package mod.chloeprime.aaaparticles.api.client;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.api.client.effekseer.Effekseer;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerEffect;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerManager;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import mod.chloeprime.aaaparticles.client.internal.CollisionCallbackSupport;
import mod.chloeprime.aaaparticles.client.render.RenderUtil;
import mod.chloeprime.aaaparticles.client.util.GlDebug;
import mod.chloeprime.aaaparticles.client.util.GlDebugIds;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

/**
 * An effect wrapper with a registry name,
 * the effect instance is mutable.
 *
 * @since 1.0.0
 * @author ChloePrime
 */
public class EffectDefinition implements Closeable {
    public EffectDefinition(@NotNull EffectMetadata metadata) {
        this.metadata = Objects.requireNonNull(metadata);
        for (ParticleEmitter.Type type : ParticleEmitter.Type.values()) {
            oneShotEmitters.put(type, new LinkedHashSet<>());
            namedEmitters.put(type, new LinkedHashMap<>());
        }
    }

    /**
     * Get the registry id of this effek.
     *
     * @return the id representing this loaded effek in {@link EffectRegistry}
     */
    @SuppressWarnings("unused")
    public ResourceLocation getId() {
        return id.get();
    }

    /**
     * Get the metadata of this effek.
     *
     * @return metadata of this effek.
     */
    @SuppressWarnings("unused")
    public EffectMetadata getMetadata() {
            return metadata;
    }

    /**
     * Create an anonymous emitter and play it.
     *
     * @return the particle emitter, a wrapper of an int handle from the Effekseer native api.
     */
    public ParticleEmitter play() {
        return play(ParticleEmitter.Type.WORLD);
    }

    /**
     * Create a named emitter and play it.
     * Created emitter can be retrieved by emitter name through {@link #getNamedEmitter(ParticleEmitter.Type, ResourceLocation)}.
     *
     * @param emitterName the name of the emitter.
     * @return the particle emitter, a wrapper of an int handle from the Effekseer native api.
     */
    public ParticleEmitter play(ResourceLocation emitterName) {
        return play(ParticleEmitter.Type.WORLD, emitterName);
    }

    /**
     * Get the registry id of this effek.
     *
     * @param type Type of this emitter.
     * @return the id representing this loaded effek in {@link EffectRegistry}
     */
    public ParticleEmitter play(ParticleEmitter.Type type) {
        if (RenderUtil.isReloadingResourcePacks()) {
            return ParticleEmitter.dummy(type);
        }
        var emitter = getManager(type).createParticle(getEffect(), type);
        var collection = Objects.requireNonNull(oneShotEmitters.get(type));
        collection.add(emitter);
        return emitter;
    }

    /**
     * Create a named emitter and play it.
     * Created emitter can be retrieved by emitter name through {@link #getNamedEmitter(ParticleEmitter.Type, ResourceLocation)}.
     *
     * @param type Type of this emitter.
     * @param emitterName the name of the emitter.
     * @return the particle emitter, a wrapper of an int handle from the Effekseer native api.
     */
    public ParticleEmitter play(ParticleEmitter.Type type, ResourceLocation emitterName) {
        if (RenderUtil.isReloadingResourcePacks()) {
            return ParticleEmitter.dummy(type);
        }
        var emitter = getManager(type).createParticle(getEffect(), type);
        var collection = Objects.requireNonNull(namedEmitters.get(type));
        var old = collection.put(emitterName, emitter);
        if (old != null) {
            old.stop();
        }
        return emitter;
    }

    /**
     * Get an emitter through its name.
     *
     * @param type Type of this emitter.
     * @param emitterName the name of the emitter.
     * @return the particle emitter with the given emitter name. Empty if not exist.
     */
    public Optional<ParticleEmitter> getNamedEmitter(ParticleEmitter.Type type, ResourceLocation emitterName) {
        return Optional.ofNullable(namedEmitters.get(type).get(emitterName));
    }

    /**
     * Get the underlying effekseer manager of this effek.
     *
     * @param type Type of this emitter. Each particle type has its own manager.
     * @return the manager of this emitter type.
     */
    public EffekseerManager getManager(ParticleEmitter.Type type) {
        if (SHUTDOWN.get()) {
            throw new IllegalStateException("Accessing %s after it has been shutdown".formatted(EffectDefinition.class.getSimpleName()));
        }
        return Objects.requireNonNull(THE_ONE_MANAGERS.get().get(type));
    }

    /**
     * Get all existing emitters of this effek.
     * May contain some finished emitters pending to be cleaned.
     *
     * @return all existing emitters of this effek.
     */
    public Stream<ParticleEmitter> emitters() {
        return emitterContainers().flatMap(Collection::stream);
    }

    /**
     * Get all existing emitters of this effek of a given type.
     * May contain some finished emitters pending to be cleaned.
     *
     * @param type Type of this emitter.
     * @return all existing emitters of this effek with the given type.
     */
    public Stream<ParticleEmitter> emitters(ParticleEmitter.Type type) {
        return emitterContainers(type).flatMap(Collection::stream);
    }

    /**
     * Get all existing emitters of this effek, both one-shot and named.
     *
     * @return all existing emitters of this effek, both one-shot and named.
     */
    public Stream<Collection<ParticleEmitter>> emitterContainers() {
        return Stream.concat(
                oneShotEmitters.values().stream(),
                namedEmitters.values().stream().map(Map::values)
        );
    }

    /**
     * Get all existing emitters of this effek under the given type, both one-shot and named.
     *
     * @param type Wanted emitter type.
     * @return all existing emitters of this effek under the given type, both one-shot and named.
     */
    public Stream<Collection<ParticleEmitter>> emitterContainers(ParticleEmitter.Type type) {
        var oneshot = Objects.requireNonNull(oneShotEmitters.get(type));
        var named   = Objects.requireNonNull(namedEmitters.get(type)).values();
        return Stream.of(oneshot, named);
    }

    /**
     * Get the direct wrapper of the underlying Effekseer effect.
     *
     * @apiNote Do not keep reference of its return value.
     * Actual effect may be updated upon resource pack reloads.
     *
     * @return the effect that can be played directly.
     */
    public EffekseerEffect getEffect() {
        return effect;
    }

    @ApiStatus.Internal
    public EffectDefinition setEffect(EffekseerEffect effect) {
        Objects.requireNonNull(effect);
        if (this.effect == effect) {
            return this;
        }
        if (SHUTDOWN.get()) {
            this.effect = effect;
            return this;
        }
        THE_ONE_MANAGERS.get();
        // If this is not the first time of load.
        if (this.effect != null) {
            emitters().forEach(ParticleEmitter::stop);
            this.effect.close();
        }
        this.effect = effect;
        return this;
    }

    /**
     * Get all Effekseer managers of all emitter types.
     *
     * @return all Effekseer managers of all emitter types.
     * @deprecated since 2.2.0, managers are single instanced. Use {{@link #globalManagers()}} instead.
     */
    @Deprecated(forRemoval = true, since = "2.2.0")
    public Stream<EffekseerManager> managers() {
        return globalManagers().stream();
    }

    /**
     * Get all Effekseer managers of all emitter types.
     *
     * @return all Effekseer managers of all emitter types.
     * @since 2.2.0
     */
    public static Collection<EffekseerManager> globalManagers() {
        if (SHUTDOWN.get() || NativePlatform.isRunningOnUnsupportedPlatform()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(THE_ONE_MANAGERS.get().values());
    }

    private final Supplier<ResourceLocation> id = Suppliers.memoize(this::fetchId);
    private static final String GL_DEBUG_LABEL = "%s Begin Rendering Effeks".formatted(AAAParticles.LOG_PREFIX);
    private static final String GL_UNLOAD_MANAGER_LABEL = "%s Unloading Effekseer managers".formatted(AAAParticles.LOG_PREFIX);
    private final Supplier<String> glUnloadLabel = Suppliers.memoize(() -> "%s Unloading effek %s".formatted(AAAParticles.LOG_PREFIX, id.get()));


    private EffekseerEffect effect;
    private static final Supplier<EnumMap<ParticleEmitter.Type, EffekseerManager>> THE_ONE_MANAGERS = Suppliers.memoize(EffectDefinition::initGlobalManagers);
    private static final AtomicBoolean SHUTDOWN = new AtomicBoolean();
    private final EnumMap<ParticleEmitter.Type, Set<ParticleEmitter>> oneShotEmitters = new EnumMap<>(ParticleEmitter.Type.class);
    private final EnumMap<ParticleEmitter.Type, Map<ResourceLocation, ParticleEmitter>> namedEmitters = new EnumMap<>(ParticleEmitter.Type.class);
    private static final RandomGenerator RNG = new Random();
    private static final int GC_DELAY = 20;
    private final int magicLoadBalancer = Math.abs(RNG.nextInt() >>> 2) % GC_DELAY;
    private int gcTicks;
    private final EffectMetadata metadata;
    private static final EnumMap<ParticleEmitter.Type, MutableInt> BACKGROUND_COLOR_IDS = new EnumMap<>(ParticleEmitter.Type.class);
    private static final EnumMap<ParticleEmitter.Type, MutableInt> BACKGROUND_DEPTH_IDS = new EnumMap<>(ParticleEmitter.Type.class);
    private static final List<EffectDefinition> DEFINITION_BUFFER = new ArrayList<>(64);
    private static final List<ParticleEmitter> EMITTERS_BUFFER = new ArrayList<>(64);

    private @Nullable ResourceLocation fetchId() {
        return EffectRegistry.entries().stream()
                .flatMap(kvp -> kvp.getValue().lazyGet()
                        .map(ed -> Pair.of(kvp.getKey(), ed))
                        .stream())
                .filter(pair -> pair.getRight() == this)
                .findFirst()
                .map(Pair::getKey)
                .orElse(null);
    }

    @ApiStatus.Internal
    public static void draw(
            ParticleEmitter.Type type,
            Vector3f front, Vector3f pos,
            int w, int h, float[] camera, float[] projection,
            float deltaFrames, float partialTicks,
            @Nullable RenderTarget background
    ) {
        if (SHUTDOWN.get() || NativePlatform.isRunningOnUnsupportedPlatform()) {
            return;
        }
        var manager = Objects.requireNonNull(THE_ONE_MANAGERS.get().get(type));
        manager.startUpdate();

        manager.setViewport(w, h);
        manager.setCameraMatrix(camera);
        manager.setProjectionMatrix(projection);
        manager.setCameraParameter(
                front.x, front.y, front.z,
                pos.x, pos.y, pos.z
        );

        var backgroundColorId = BACKGROUND_COLOR_IDS.get(type);
        var backgroundDepthId = BACKGROUND_DEPTH_IDS.get(type);

        if (background == null) {
            unsetBackgrounds(manager, backgroundColorId, backgroundDepthId);
        } else if (background.getColorTextureId() != backgroundColorId.intValue() || background.getDepthTextureId() != backgroundDepthId.intValue()) {
            unsetBackgrounds(manager, backgroundColorId, backgroundDepthId);
            // Update recorded values
            backgroundColorId.setValue(background.getColorTextureId());
            backgroundDepthId.setValue(background.getDepthTextureId());
            // Update actual values of background/depth
            manager.getImpl().SetBackground(backgroundColorId.intValue(), false);
            manager.getImpl().SetDepth(backgroundDepthId.intValue(), false);
        } else {
            manager.getImpl().SetDepth(backgroundDepthId.intValue(), false);
        }

        // Load all definitions & emitters
        DEFINITION_BUFFER.clear();
        EMITTERS_BUFFER.clear();
        EffectRegistry.entries().stream()
                .map(Map.Entry::getValue)
                .map(EffectHolder::lazyGet)
                .flatMap(Optional::stream)
                .forEach(DEFINITION_BUFFER::add);
        DEFINITION_BUFFER.stream()
                .flatMap(buf -> buf.emitters(type))
                .forEach(EMITTERS_BUFFER::add);

        manager.getImpl().SetLayerParameter(1, pos.x, pos.y, pos.z, 0);
        EMITTERS_BUFFER.forEach(emitter -> emitter.internalUpdateProgress(deltaFrames));
        manager.update(deltaFrames);

        EMITTERS_BUFFER.forEach(emitter -> emitter.runPreDrawCallbacks(partialTicks));
        GlDebug.pushDebugGroup(GlDebugIds.EFFEK_DRAWING, GL_DEBUG_LABEL::toString);
        manager.draw();
        GlDebug.popDebugGroup();

        if (type == ParticleEmitter.Type.WORLD) {
            DEFINITION_BUFFER.forEach(EffectDefinition::tryCleanupEmitters);
        }

        DEFINITION_BUFFER.clear();
        EMITTERS_BUFFER.clear();

        manager.endUpdate();
    }

    private void tryCleanupEmitters() {
        gcTicks = (gcTicks + 1) % GC_DELAY;
        if (gcTicks == magicLoadBalancer) {
            emitterContainers().forEach(container -> container.removeIf(emitter -> !emitter.exists()));
        }
    }

    private static void unsetBackgrounds(EffekseerManager manager, MutableInt backgroundColorId, MutableInt backgroundDepthId) {
        if (backgroundColorId.intValue() != -1) {
            backgroundColorId.setValue(-1);
            manager.getImpl().UnsetBackground();
        }
        if (backgroundDepthId.intValue() != -1) {
            backgroundDepthId.setValue(-1);
            manager.getImpl().UnsetDepth();
        }
    }

    private static EnumMap<ParticleEmitter.Type, EffekseerManager> initGlobalManagers() {
        if (NativePlatform.isRunningOnUnsupportedPlatform()) {
            return new EnumMap<>(ParticleEmitter.Type.class);
        }
        AAAParticles.LOGGER.info("{} Initializing global Effekseer managers", AAAParticles.LOG_PREFIX);
        RenderSystem.assertOnRenderThread();
        var map = new EnumMap<ParticleEmitter.Type, EffekseerManager>(ParticleEmitter.Type.class);
        for (var type : ParticleEmitter.Type.values()) {
            BACKGROUND_COLOR_IDS.put(type, new MutableInt(-1));
            BACKGROUND_DEPTH_IDS.put(type, new MutableInt(-1));
            map.put(type, new EffekseerManager());
        }
        var world = Objects.requireNonNull(map.get(ParticleEmitter.Type.WORLD));
        var fpvMh = Objects.requireNonNull(map.get(ParticleEmitter.Type.FIRST_PERSON_MAINHAND));
        var fpvOh = Objects.requireNonNull(map.get(ParticleEmitter.Type.FIRST_PERSON_OFFHAND));
        if (!world.init(100_0000)) {
            throw new IllegalStateException("Failed to initialize EffekseerManager");
        }
        if (!fpvMh.init(500)) {
            throw new IllegalStateException("Failed to initialize (fpv mainhand) EffekseerManager");
        }
        if (!fpvOh.init(500)) {
            throw new IllegalStateException("Failed to initialize (fpv offhand) EffekseerManager");
        }
        world.setCollisionCallback(CollisionCallbackSupport.Impl.DEFAULT_TRACER);
        var cpus = Runtime.getRuntime().availableProcessors();
        var isPoorCpu = cpus < 12;
        if (isPoorCpu) {
            if (cpus > 5) {
                world.setupWorkerThreads(cpus - 4);
            }
        } else {
            world.setupWorkerThreads(cpus - 8);
            fpvMh.setupWorkerThreads(2);
            fpvOh.setupWorkerThreads(2);
        }
        return map;
    }

    /**
     * Dispose this effek definition.
     */
    @Override
    public void close() {
        emitters().forEach(ParticleEmitter::stop);
        GlDebug.pushDebugGroup(GlDebugIds.EFFEK_UNLOADING, glUnloadLabel);
        effect.close();
        GlDebug.popDebugGroup();
    }

    @ApiStatus.Internal
    public static void shutdown() {
        if (SHUTDOWN.getAndSet(true)) {
            return;
        }
        if (NativePlatform.isRunningOnUnsupportedPlatform()) {
            return;
        }

        // Shutdown Managers
        AAAParticles.LOGGER.info("{} Shutting down global Effekseer managers", AAAParticles.LOG_PREFIX);
        GlDebug.pushDebugGroup(GlDebugIds.EFFEK_MANAGER_UNLOADING, GL_UNLOAD_MANAGER_LABEL::toString);
        THE_ONE_MANAGERS.get().values().forEach(manager -> {
            manager.stopAllEffects();
            manager.close();
        });
        GlDebug.popDebugGroup();

        // Shutdown Effects
        AAAParticles.LOGGER.info("{} Shutting down Effekseer effects", AAAParticles.LOG_PREFIX);
        EffectRegistry.entries().stream()
                .map(Map.Entry::getValue)
                .map(EffectHolder::lazyGet)
                .flatMap(Optional::stream)
                .forEach(EffectDefinition::close);

        // Shutdown Backend
        AAAParticles.LOGGER.info("{} Shutting down Effekseer backend", AAAParticles.LOG_PREFIX);
        Effekseer.terminate();

        AAAParticles.LOGGER.info("{} Shutdown complete", AAAParticles.LOG_PREFIX);
    }
}
