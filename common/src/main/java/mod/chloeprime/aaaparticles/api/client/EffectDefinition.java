package mod.chloeprime.aaaparticles.api.client;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.pipeline.RenderTarget;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerEffect;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerManager;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
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
        return Objects.requireNonNull(managers.get(type));
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

    /**
     * Get all Effekseer managers of all emitter types.
     * @return all Effekseer managers of all emitter types.
     */
    public Stream<EffekseerManager> managers() {
        return managers.values().stream();
    }

    private final Supplier<ResourceLocation> id = Suppliers.memoize(this::fetchId);
    private final Supplier<String> glDebugLabel = Suppliers.memoize(() -> "%s Begin Rendering Effek %s".formatted(AAAParticles.LOG_PREFIX, id.get()));
    private final Supplier<String> glUnloadManagerLabel = Suppliers.memoize(() -> "%s Unloading managers for effek %s".formatted(AAAParticles.LOG_PREFIX, id.get()));
    private final Supplier<String> glUnloadLabel = Suppliers.memoize(() -> "%s Unloading effek %s".formatted(AAAParticles.LOG_PREFIX, id.get()));

    private EffekseerEffect effect;
    private final EnumMap<ParticleEmitter.Type, EffekseerManager> managers = new EnumMap<>(ParticleEmitter.Type.class);
    private final EnumMap<ParticleEmitter.Type, Set<ParticleEmitter>> oneShotEmitters = new EnumMap<>(ParticleEmitter.Type.class);
    private final EnumMap<ParticleEmitter.Type, Map<ResourceLocation, ParticleEmitter>> namedEmitters = new EnumMap<>(ParticleEmitter.Type.class);
    private static final RandomGenerator RNG = new Random();
    private static final int GC_DELAY = 20;
    private final int magicLoadBalancer = Math.abs(RNG.nextInt() >>> 2) % GC_DELAY;
    private int gcTicks;
    private final EffectMetadata metadata;
    private final EnumMap<ParticleEmitter.Type, MutableInt> backgroundColorIds = new EnumMap<>(ParticleEmitter.Type.class);
    private final EnumMap<ParticleEmitter.Type, MutableInt> backgroundDepthIds = new EnumMap<>(ParticleEmitter.Type.class);

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
    public void draw(
            ParticleEmitter.Type type,
            Vector3f front, Vector3f pos,
            int w, int h, float[] camera, float[] projection,
            float deltaFrames, float partialTicks,
            @Nullable RenderTarget background
    ) {
        var manager = Objects.requireNonNull(managers.get(type));
        manager.startUpdate();

        manager.setViewport(w, h);
        manager.setCameraMatrix(camera);
        manager.setProjectionMatrix(projection);
        manager.setCameraParameter(
                front.x, front.y, front.z,
                pos.x, pos.y, pos.z
        );

        var backgroundColorId = backgroundColorIds.get(type);
        var backgroundDepthId = backgroundDepthIds.get(type);

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

        manager.getImpl().SetLayerParameter(1, pos.x, pos.y, pos.z, 0);
        emitters(type).forEach(emitter -> emitter.internalUpdateProgress(deltaFrames));
        manager.update(deltaFrames);

        emitters(type).forEach(emitter -> emitter.runPreDrawCallbacks(partialTicks));
        GlDebug.pushDebugGroup(GlDebugIds.EFFEK_DRAWING, glDebugLabel);
        manager.draw();
        GlDebug.popDebugGroup();

        if (type == ParticleEmitter.Type.WORLD) {
            gcTicks = (gcTicks + 1) % GC_DELAY;
            if (gcTicks == magicLoadBalancer) {
                emitterContainers().forEach(container -> container.removeIf(emitter -> !emitter.exists()));
            }
        }

        manager.endUpdate();
    }

    private void unsetBackgrounds(EffekseerManager manager, MutableInt backgroundColorId, MutableInt backgroundDepthId) {
        if (backgroundColorId.intValue() != -1) {
            backgroundColorId.setValue(-1);
            manager.getImpl().UnsetBackground();
        }
        if (backgroundDepthId.intValue() != -1) {
            backgroundDepthId.setValue(-1);
            manager.getImpl().UnsetDepth();
        }
    }

    private void unsetBackgrounds(ParticleEmitter.Type type) {
        unsetBackgrounds(managers.get(type), backgroundColorIds.get(type), backgroundDepthIds.get(type));
    }

    private void initManager() {
        for (ParticleEmitter.Type type : ParticleEmitter.Type.values()) {
            backgroundColorIds.put(type, new MutableInt(-1));
            backgroundDepthIds.put(type, new MutableInt(-1));
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
        worldManager.setCollisionCallback(CollisionCallbackSupport.Impl.DEFAULT_TRACER);
        worldManager.setupWorkerThreads(2);
        fpvMhManager.setupWorkerThreads(1);
        fpvOhManager.setupWorkerThreads(1);
    }

    /**
     * Dispose this effek definition.
     */
    @Override
    public void close() {
        Arrays.stream(ParticleEmitter.Type.values()).forEach(this::unsetBackgrounds);
        GlDebug.pushDebugGroup(GlDebugIds.EFFEK_MANAGER_UNLOADING, glUnloadManagerLabel);
        managers.values().forEach(EffekseerManager::close);
        GlDebug.popDebugGroup();
        GlDebug.pushDebugGroup(GlDebugIds.EFFEK_UNLOADING, glUnloadLabel);
        effect.close();
        GlDebug.popDebugGroup();
    }
}
