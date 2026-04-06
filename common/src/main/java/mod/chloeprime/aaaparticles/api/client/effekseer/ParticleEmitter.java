package mod.chloeprime.aaaparticles.api.client.effekseer;

import mod.chloeprime.aaaparticles.client.internal.DummyParticleEmitter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Wrapper for the int handle of native particle emitters.
 *
 * @since 1.0.0
 * @author ChloePrime
 */
@SuppressWarnings("unused")
public class ParticleEmitter {
    public enum Type {
        /**
         * Particles in the world.
         */
        WORLD,

        /**
         * Particles bound to the mainhand.
         * WARNING: Hand effeks is an experimental functionality.
         */
        @ApiStatus.Experimental
        FIRST_PERSON_MAINHAND,

        /**
         * Particles bound to the offhand.
         * WARNING: Hand effeks is an experimental functionality.
         */
        @ApiStatus.Experimental
        FIRST_PERSON_OFFHAND,
    }

    /**
     * Get a dummy emitter that is not bound to any native emitter handles.
     */
    public static ParticleEmitter dummy(ParticleEmitter.Type type) {
        if (DUMMIES.isEmpty()) {
            for (Type _ty : Type.values()) {
                DUMMIES.put(_ty, new DummyParticleEmitter(_ty));
            }
        }
        return DUMMIES.get(type);
    }

    /**
     * Pause this effek emitter and its effek instance.
     */
    public void pause() {
        if (isValid) {
            manager.getImpl().SetPaused(this.handle, true);
        }
        isPaused = true;
    }

    /**
     * Resume this effek emitter and its effek instance from pausing.
     */
    public void resume() {
        if (isValid) {
            manager.getImpl().SetPaused(this.handle, false);
        }
        isPaused = false;
    }

    /**
     * Get whether this emitter is paused.
     *
     * @return whether this emitter is paused.
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Get whether this emitter is not explicitly hidden through {@link #setVisibility(boolean)}.
     *
     * @return whether this emitter is not explicitly hidden.
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Set this emitter's own visibility.
     *
     * @param visible if false, this particle will be invisible.
     */
    public void setVisibility(boolean visible) {
        if (isValid) {
            manager.getImpl().SetShown(this.handle, visible);
        }
        isVisible = visible;
    }

    /**
     * Stop and destroy this emitter.
     */
    public void stop() {
        if (isValid) {
            isValid = false;
            manager.getImpl().Stop(this.handle);
        }
    }

    /**
     * Set the progress of this emitter.
     */
    public void setProgress(float frame) {
        if (isValid) {
            this.frame = frame;
            manager.getImpl().UpdateHandleToMoveToFrame(this.handle, frame);
        }
    }

    /**
     * Set the world position of this emitter.
     *
     * @param x X coordinate of the new position.
     * @param y Y coordinate of the new position.
     * @param z Z coordinate of the new position.
     */
    public void setPosition(float x, float y, float z) {
        if (isValid) {
            manager.getImpl().SetEffectPosition(this.handle, x, y, z);
        }
    }

    /**
     * Set the rotation of this emitter.
     *
     * @param x X rotation of the new position.
     * @param y Y rotation of the new position.
     * @param z Z rotation of the new position.
     */
    public void setRotation(float x, float y, float z) {
        if (isValid) {
            manager.getImpl().SetEffectRotation(this.handle, x, y, z);
        }
    }

    /**
     * Set the rotation of this emitter.
     *
     * @param x X scale of the new position.
     * @param y Y scale of the new position.
     * @param z Z scale of the new position.
     */
    public void setScale(float x, float y, float z) {
        if (isValid) {
            manager.getImpl().SetEffectScale(this.handle, x, y, z);
        }
    }

    /**
     * Set the transform matrix of this emitter.
     *
     * @param matrix the flattened transform matrix.
     */
    public void setTransformMatrix(float[] matrix) {
        if (isValid) {
            manager.getImpl().SetEffectTransformMatrix(
                    this.handle,
                    matrix[0], matrix[1], matrix[2], matrix[3],
                    matrix[4], matrix[5], matrix[6], matrix[7],
                    matrix[8], matrix[9], matrix[10], matrix[11]
            );
        }
    }

    /**
     * Set the transform matrix of this emitter.
     *
     * @param matrix the transform matrix.
     */
    public void setTransformMatrix(float[][] matrix) {
        if (isValid) {
            int i = 0;
            manager.getImpl().SetEffectTransformMatrix(
                    this.handle,
                    matrix[i][0], matrix[i][1], matrix[i][2], matrix[i++][3],
                    matrix[i][0], matrix[i][1], matrix[i][2], matrix[i++][3],
                    matrix[i][0], matrix[i][1], matrix[i][2], matrix[i][3]
            );
        }
    }

    /**
     * Set the base transform matrix of this emitter.
     *
     * @param matrix the flattened base transform matrix.
     */
    public void setBaseTransformMatrix(float[] matrix) {
        if (isValid) {
            manager.getImpl().SetEffectTransformBaseMatrix(
                    this.handle,
                    matrix[0], matrix[1], matrix[2], matrix[3],
                    matrix[4], matrix[5], matrix[6], matrix[7],
                    matrix[8], matrix[9], matrix[10], matrix[11]
            );
        }
    }

    /**
     * Set the base transform matrix of this emitter.
     *
     * @param matrix the base transform matrix.
     */
    public void setBaseTransformMatrix(float[][] matrix) {
        if (isValid) {
            int i = 0;
            manager.getImpl().SetEffectTransformBaseMatrix(
                    this.handle,
                    matrix[i][0], matrix[i][1], matrix[i][2], matrix[i++][3],
                    matrix[i][0], matrix[i][1], matrix[i][2], matrix[i++][3],
                    matrix[i][0], matrix[i][1], matrix[i][2], matrix[i][3]
            );
        }
    }

    /**
     * Get whether this emitter exists in the effek manager.
     *
     * @return whether this emitter exists in the effek manager.
     */
    public boolean exists() {
        return isValid && manager.getImpl().Exists(this.handle);
    }

    /**
     * Set a dynamic input's value.
     *
     * @param index The index of the dynamic input. Valid range is 0-3.
     * @param value The new value of the dynamic input.
     */
    public void setDynamicInput(int index, float value) {
        if (isValid) {
            manager.getImpl().SetDynamicInput(this.handle, index, value);
        }
    }

    /**
     * Get a dynamic input's value.
     *
     * @param index The index of the dynamic input. Valid range is 0-3.
     * @return The value of the dynamic input.
     */
    public float getDynamicInput(int index) {
        if (isValid) {
            return manager.getImpl().GetDynamicInput(this.handle, index);
        } else {
            return 0;
        }
    }

    /**
     * Trigger a trigger with the given index.
     *
     * @param index The index of the trigger. Valid range is 0-3.
     */
    public void sendTrigger(int index) {
        if (isValid) {
            manager.getImpl().SendTrigger(this.handle, index);
        }
    }

    /**
     * Get the speed scale of this emitter.
     *
     * @implNote Speed scaling is fully implemented on the Java side, by changing the emitter's progress when updating.
     * @since 2.0.0
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Set relative play speed of this emitter.
     * <p>
     * WARNING: Effekseer effects are baked as 60 frames (by default), and
     * changing this value to lower than {@code 1} may look lagged / stepped.
     * <p>
     * To fix this problem, you should set the play speed in your Effekseer editor
     * as the slowest desired play speed, and call this method with the argument value not lesser than {@code 1},
     * up to your max desired relative play speed.
     * <p>
     * WARNING: Do not set speed on long-time emitters.
     * Emitters with relative speed other than {@code 1} (default)
     * will increase performance cost by time, until it has been stopped.
     *
     * @implNote Speed scaling is fully implemented on the Java side, by changing the emitter's progress when updating.
     * @since 2.0.0
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Function type of pre-draw callbacks.
     */
    @FunctionalInterface
    public interface PreDrawCallback extends BiConsumer<ParticleEmitter, Float> {
        /**
         * @see java.util.function.BiConsumer#accept(Object, Object)
         */
        void accept(ParticleEmitter emitter, float partialTicks);

        @Override
        @Deprecated
        default void accept(ParticleEmitter emitter, Float partialTicks) {
            accept(emitter, partialTicks.floatValue());
        }

        /**
         * @see java.util.function.BiConsumer#andThen(BiConsumer)  
         */
        default PreDrawCallback andThen(PreDrawCallback after) {
            Objects.requireNonNull(after);
            return (emitter, partial) -> { accept(emitter, partial); after.accept(emitter, partial); };
        }
    }

    /**
     * Add a callback that is called before effeks are drawn.
     *
     * @implNote Callbacks are fully implemented on the Java side.
     * @param callback the callback function.
     */
    public void addPreDrawCallback(PreDrawCallback callback) {
        Optional.ofNullable(this.callback).ifPresentOrElse(
                c -> this.callback = this.callback.andThen(c),
                () -> this.callback = callback
        );
    }

    private static final EnumMap<ParticleEmitter.Type, ParticleEmitter> DUMMIES = new EnumMap<>(ParticleEmitter.Type.class);

    public final int handle;
    public final Type type;
    private float speed = 1;
    private float frame = 0;

    private final EffekseerManager manager;
    private @Nullable PreDrawCallback callback;
    // Kept public for compatibility reason
    @ApiStatus.Internal public boolean isVisible = true;
    @ApiStatus.Internal public boolean isPaused = false;
    private boolean isValid = true;

    protected ParticleEmitter(int handle, EffekseerManager manager, Type type) {
        this.handle = handle;
        this.manager = manager;
        this.type = type;
        setVisibility(true);
        resume();
    }

    @ApiStatus.Internal
    public void runPreDrawCallbacks(float partial) {
        Optional.ofNullable(this.callback).ifPresent(c -> c.accept(this, partial));
    }

    @ApiStatus.Internal
    public void internalUpdateProgress(float deltaFrames) {
        var speed = getSpeed();
        frame += deltaFrames * speed;
        if (Math.abs(speed - 1) > 1e-6) {
            setProgress(frame);
        }
    }
}