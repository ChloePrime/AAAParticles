package mod.chloeprime.aaaparticles.api.client.effekseer;

import Effekseer.swig.EffekseerCoreDeviceType;

public enum DeviceType {
    UNKNOWN(EffekseerCoreDeviceType.Unknown),
    OPENGL(EffekseerCoreDeviceType.OpenGL);

    public int getNativeOrdinal() {
        return impl.swigValue();
    }

    public static DeviceType fromNativeOrdinal(int ord) {
        for (DeviceType value : values()){
            if (value.getNativeOrdinal() == ord) {
                return value;
            }
        }
        throw new IllegalArgumentException("DeviceType.fromNativeOrdinal: ord = " + ord);
    }

    DeviceType(EffekseerCoreDeviceType impl) {
        this.impl = impl;
    }

    private final EffekseerCoreDeviceType impl;
}
