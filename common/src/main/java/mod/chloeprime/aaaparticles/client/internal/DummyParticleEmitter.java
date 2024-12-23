package mod.chloeprime.aaaparticles.client.internal;

import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;

/**
 * No-OP emitter, created when trying to play effeks during resource pack reloading.
 * @author ChloePrime
 */
@SuppressWarnings("unused")
public class DummyParticleEmitter extends ParticleEmitter {
    public DummyParticleEmitter(Type type) {
        super(-1, null, type);
    }

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void resume() {
        isPaused = false;
    }

    @Override
    public void setVisibility(boolean visible) {
        isVisible = visible;
    }

    @Override
    public void stop() {
    }

    @Override
    public void setProgress(float frame) {
    }

    @Override
    public void setPosition(float x, float y, float z) {
    }

    @Override
    public void setRotation(float x, float y, float z) {
    }

    @Override
    public void setScale(float x, float y, float z) {
    }

    @Override
    public void setTransformMatrix(float[] matrix) {
    }

    @Override
    public void setTransformMatrix(float[][] matrix) {
    }

    public void setBaseTransformMatrix(float[] matrix) {
    }

    @Override
    public void setBaseTransformMatrix(float[][] matrix) {
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public void setDynamicInput(int index, float value) {
    }

    @Override
    public float getDynamicInput(int index) {
        return 0;
    }

    @Override
    public void sendTrigger(int index) {
    }
}