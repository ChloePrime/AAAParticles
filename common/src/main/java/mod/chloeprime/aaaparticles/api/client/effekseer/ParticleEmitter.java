package mod.chloeprime.aaaparticles.api.client.effekseer;

import mod.chloeprime.aaaparticles.client.internal.DummyParticleEmitter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Objects;
import java.util.Optional;

/**
 * Wrapper for the int handle of native particle emitters.
 * @author ChloePrime
 */
@SuppressWarnings("unused")
public class ParticleEmitter {
    public enum Type {
        WORLD,
        FIRST_PERSON_MAINHAND,
        FIRST_PERSON_OFFHAND,
    }

    public final int handle;
    public final Type type;

    private final EffekseerManager manager;
    private @Nullable PreDrawCallback callback;
    // Kept public for compatibility reason
    @ApiStatus.Internal public boolean isVisible = true;
    @ApiStatus.Internal public boolean isPaused = false;
    private boolean isValid = true;

    public static ParticleEmitter dummy(ParticleEmitter.Type type) {
        if (DUMMIES.isEmpty()) {
            for (Type _ty : Type.values()) {
                DUMMIES.put(_ty, new DummyParticleEmitter(_ty));
            }
        }
        return DUMMIES.get(type);
    }

    private static final EnumMap<ParticleEmitter.Type, ParticleEmitter> DUMMIES = new EnumMap<>(ParticleEmitter.Type.class);

    protected ParticleEmitter(int handle, EffekseerManager manager, Type type) {
        this.handle = handle;
        this.manager = manager;
        this.type = type;
        setVisibility(true);
        resume();
    }

    public void pause() {
        if (isValid) {
            manager.getImpl().SetPaused(this.handle, true);
        }
        isPaused = true;
    }

    public void resume() {
        if (isValid) {
            manager.getImpl().SetPaused(this.handle, false);
        }
        isPaused = false;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisibility(boolean visible) {
        if (isValid) {
            manager.getImpl().SetShown(this.handle, visible);
        }
        isVisible = visible;
    }

    public void stop() {
        if (isValid) {
            isValid = false;
            manager.getImpl().Stop(this.handle);
        }
    }

    public void setProgress(float frame) {
        if (isValid) {
            manager.getImpl().UpdateHandleToMoveToFrame(this.handle, frame);
        }
    }

    public void setPosition(float x, float y, float z) {
        if (isValid) {
            manager.getImpl().SetEffectPosition(this.handle, x, y, z);
        }
    }

    public void setRotation(float x, float y, float z) {
        if (isValid) {
            manager.getImpl().SetEffectRotation(this.handle, x, y, z);
        }
    }

    public void setScale(float x, float y, float z) {
        if (isValid) {
            manager.getImpl().SetEffectScale(this.handle, x, y, z);
        }
    }

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

    public boolean exists() {
        return isValid && manager.getImpl().Exists(this.handle);
    }

    public void setDynamicInput(int index, float value) {
        if (isValid) {
            manager.getImpl().SetDynamicInput(this.handle, index, value);
        }
    }

    public float getDynamicInput(int index) {
        if (isValid) {
            return manager.getImpl().GetDynamicInput(this.handle, index);
        } else {
            return 0;
        }
    }

    public void sendTrigger(int index) {
        if (isValid) {
            manager.getImpl().SendTrigger(this.handle, index);
        }
    }

    public interface PreDrawCallback {
        void accept(ParticleEmitter emitter, float partialTicks);

        default PreDrawCallback andThen(PreDrawCallback after) {
            Objects.requireNonNull(after);
            return (emitter, partial) -> { accept(emitter, partial); after.accept(emitter, partial); };
        }
    }

    public void addPreDrawCallback(PreDrawCallback callback) {
        Optional.ofNullable(this.callback).ifPresentOrElse(
                c -> this.callback = this.callback.andThen(c),
                () -> this.callback = callback
        );
    }

    public void runPreDrawCallbacks(float partial) {
        Optional.ofNullable(this.callback).ifPresent(c -> c.accept(this, partial));
    }
}