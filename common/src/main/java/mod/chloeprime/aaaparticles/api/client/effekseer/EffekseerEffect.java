package mod.chloeprime.aaaparticles.api.client.effekseer;

import Effekseer.swig.EffekseerBackendCore;
import Effekseer.swig.EffekseerEffectCore;
import mod.chloeprime.aaaparticles.common.util.Helpers;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author ChloePrime
 */
@SuppressWarnings("unused")
public class EffekseerEffect extends SafeFinalized<EffekseerEffectCore> {
    protected final EffekseerEffectCore impl;

    private boolean isLoaded = false;

    public EffekseerEffect() {
        this(Helpers.checkPlatform(EffekseerEffectCore::new));
    }

    @Override
    public void close() {
        impl.delete();
    }

    public boolean load(InputStream stream, float amplifier) throws IOException {
        byte[] bytes = stream.readAllBytes();
        return load(bytes, bytes.length, amplifier);
    }

    public boolean load(byte[] data, int length, float amplifier) {
        isLoaded = impl.Load(data, length, amplifier);
        return isLoaded;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public boolean loadTexture(InputStream stream, int index, TextureType type) throws IOException {
        byte[] bytes = stream.readAllBytes();
        return loadTexture(bytes, bytes.length, index, type);
    }

    public boolean loadTexture(byte[] data, int length, int index, TextureType type) {
        return impl.LoadTexture(data, length, index, type.getImpl());
    }

    public boolean loadCurve(InputStream stream, int index) throws IOException {
        byte[] bytes = stream.readAllBytes();
        return loadCurve(bytes, bytes.length, index);
    }

    public boolean loadCurve(byte[] data, int length, int index) {
        return impl.LoadCurve(data, length, index);
    }

    public boolean loadMaterial(InputStream stream, int index) throws IOException {
        byte[] bytes = stream.readAllBytes();
        return loadMaterial(bytes, bytes.length, index);
    }

    public boolean loadMaterial(byte[] data, int length, int index) {
        return impl.LoadMaterial(data, length, index);
    }

    public boolean loadModel(InputStream stream, int index) throws IOException {
        byte[] bytes = stream.readAllBytes();
        return loadModel(bytes, bytes.length, index);
    }

    public boolean loadModel(byte[] data, int length, int index) {
        return impl.LoadModel(data, length, index);
    }

    public boolean isModelLoaded(int index) {
        return impl.HasModelLoaded(index);
    }

    public boolean isCurveLoaded(int index) {
        return impl.HasCurveLoaded(index);
    }

    public boolean isMaterialLoaded(int index) {
        return impl.HasMaterialLoaded(index);
    }

    public boolean isTextureLoaded(int index, TextureType type) {
        return impl.HasTextureLoaded(index, type.getImpl());
    }

    public int curveCount() {
        return impl.GetCurveCount();
    }

    public int modelCount() {
        return impl.GetModelCount();
    }

    public int materialCount() {
        return impl.GetMaterialCount();
    }

    public int textureCount(TextureType type) {
        return impl.GetTextureCount(type.getImpl());
    }

    public int textureCount() {
        int amt = 0;
        for (TextureType value : TextureType.values()) {
            amt += impl.GetTextureCount(value.getImpl());
        }
        return amt;
    }

    public String getTexturePath(int index, TextureType type) {
        return impl.GetTexturePath(index, type.getImpl());
    }

    public String getCurvePath(int index) {
        return impl.GetCurvePath(index);
    }

    public String getMaterialPath(int index) {
        return impl.GetMaterialPath(index);
    }

    public String getModelPath(int index) {
        return impl.GetModelPath(index);
    }

    public int minTerm() {
        return impl.GetTermMin();
    }

    public int maxTerm() {
        return impl.GetTermMax();
    }

    public final EffekseerEffectCore getImpl() {
        return impl;
    }

    protected EffekseerEffect(EffekseerEffectCore impl) {
        super(impl, EffekseerEffectCore::delete);
        this.impl = impl;
    }
}