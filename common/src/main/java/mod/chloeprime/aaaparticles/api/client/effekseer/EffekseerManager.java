package mod.chloeprime.aaaparticles.api.client.effekseer;

import java.nio.FloatBuffer;

import com.mojang.math.Matrix4f;

import Effekseer.swig.EffekseerManagerCore;

/**
 * @author ChloePrime
 */
public class EffekseerManager extends SafeFinalized<EffekseerManagerCore> {
    public EffekseerManager() {
        this(new EffekseerManagerCore());
    }

    public boolean init(int maxSprites, boolean srgb) {
        return impl.Initialize(maxSprites, srgb);
    }

    public boolean init(int maxSprites) {
        return impl.Initialize(maxSprites);
    }

    public void update(float delta) {
        impl.Update(delta);
    }

    public void startUpdate() {
        impl.BeginUpdate();
    }

    public void endUpdate() {
        impl.EndUpdate();
    }

    @Override
    public void close() {
        impl.delete();
    }

    public ParticleEmitter createParticle(EffekseerEffect effect) {
        return createParticle(effect, ParticleEmitter.Type.WORLD);
    }

    public ParticleEmitter createParticle(EffekseerEffect effect, ParticleEmitter.Type type) {
        int handle = impl.Play(effect.getImpl());
        return new ParticleEmitter(handle, this, type);
    }

    public void stopAllEffects() {
        impl.StopAllEffects();
    }

    public void draw() {
        drawBack();
        drawFront();
    }

    public void drawBack() {
        impl.DrawBack();
    }

    public void drawFront() {
        impl.DrawFront();
    }

    public void setViewport(int width, int height) {
        impl.SetViewProjectionMatrixWithSimpleWindow(width, height);
    }

    public void setupWorkerThreads(int count) {
        impl.LaunchWorkerThreads(count);
    }

    public void setCameraMatrix(float[] m) {
        impl.SetCameraMatrix(
                m[0],   m[1],   m[2],   m[3],
                m[4],   m[5],   m[6],   m[7],
                m[8],   m[9],   m[0xA], m[0xB],
                m[0xC], m[0xD], m[0xE], m[0xF]
        );
    }

    private static final ThreadLocal<float[]> MATRIX_BUFFER = ThreadLocal.withInitial(() -> new float[16]);


    public void setCameraMatrix(Matrix4f m) {
        var buffer = FloatBuffer.wrap(MATRIX_BUFFER.get());
        m.store(buffer);
        setCameraMatrix(buffer);
    }

    public void setCameraMatrix(FloatBuffer buf) {
        impl.SetCameraMatrix(
                buf.get(), buf.get(), buf.get(), buf.get(),
                buf.get(), buf.get(), buf.get(), buf.get(),
                buf.get(), buf.get(), buf.get(), buf.get(),
                buf.get(), buf.get(), buf.get(), buf.get()
        );
    }

    public void setProjectionMatrix(Matrix4f m) {
        var buffer = FloatBuffer.wrap(MATRIX_BUFFER.get());
        m.store(buffer);
        setProjectionMatrix(buffer);
    }

    public void setProjectionMatrix(FloatBuffer buf) {
        impl.SetProjectionMatrix(
                buf.get(), buf.get(), buf.get(), buf.get(),
                buf.get(), buf.get(), buf.get(), buf.get(),
                buf.get(), buf.get(), buf.get(), buf.get(),
                buf.get(), buf.get(), buf.get(), buf.get()
        );
    }

    public void setProjectionMatrix(float[] m) {
        impl.SetProjectionMatrix(
                m[0],   m[1],   m[2],   m[3],
                m[4],   m[5],   m[6],   m[7],
                m[8],   m[9],   m[0xA], m[0xB],
                m[0xC], m[0xD], m[0xE], m[0xF]
        );
    }

    public final EffekseerManagerCore getImpl() {
        return impl;
    }

    protected EffekseerManager(EffekseerManagerCore impl) {
        super(impl, EffekseerManagerCore::delete);
        this.impl = impl;
    }

    private final EffekseerManagerCore impl;
}
