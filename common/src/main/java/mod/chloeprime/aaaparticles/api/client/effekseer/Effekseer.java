package mod.chloeprime.aaaparticles.api.client.effekseer;

import Effekseer.swig.EffekseerBackendCore;

import java.io.Closeable;

/**
 * @author ChloePrime
 */
@SuppressWarnings("unused")
public class Effekseer implements Closeable {

    public static boolean init() {
        return EffekseerBackendCore.InitializeWithOpenGL();
    }

    public static void terminate() {
        EffekseerBackendCore.Terminate();
    }

    public static DeviceType getDeviceType() {
        return DeviceType.fromNativeOrdinal(EffekseerBackendCore.GetDevice().swigValue());
    }

    @Override
    public void close() {
        impl.delete();
    }

    public final EffekseerBackendCore getImpl() {
        return impl;
    }

    private final EffekseerBackendCore impl = new EffekseerBackendCore();
}
