package mod.chloeprime.aaaparticles.api.client.effekseer;

import Effekseer.swig.EffekseerTextureType;

@SuppressWarnings("unused")
public enum TextureType {
    COLOR(EffekseerTextureType.Color),
    NORMAL(EffekseerTextureType.Normal),
    DISTORTION(EffekseerTextureType.Distortion);

    final EffekseerTextureType impl;

    TextureType(EffekseerTextureType type) {
        this.impl = type;
    }

    @Override
    public String toString() {
        return impl.toString();
    }

    public int getNativeOrdinal() {
        return impl.swigValue();
    }

    public static TextureType fromNativeOrdinal(int ord) {
        for (TextureType value : values()) {
            if (value.getNativeOrdinal() == ord) {
                return value;
            }
        }
        return null;
    }

    public EffekseerTextureType getImpl() {
        return impl;
    }
}