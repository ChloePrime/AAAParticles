package mod.chloeprime.aaaparticles.api.client.effekseer;

import Effekseer.swig.EffekseerBackendCore;
import mod.chloeprime.aaaparticles.common.util.Helpers;

/**
 * @author ChloePrime
 */
@SuppressWarnings("unused")
public class Effekseer extends SafeFinalized<EffekseerBackendCore> {
    public Effekseer() {
        this(Helpers.checkPlatform(EffekseerBackendCore::new));
    }

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

    protected Effekseer(EffekseerBackendCore impl) {
        super(impl, EffekseerBackendCore::delete);
        this.impl = impl;
    }

    private final EffekseerBackendCore impl;
}
