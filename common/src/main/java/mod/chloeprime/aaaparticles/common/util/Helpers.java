package mod.chloeprime.aaaparticles.common.util;

import mod.chloeprime.aaaparticles.client.installer.NativePlatform;

import java.util.function.Supplier;

public class Helpers {
    public static <T> T checkPlatform(Supplier<T> constructor) {
        if (NativePlatform.isRunningOnUnsupportedPlatform()) {
            throw new UnsupportedOperationException("Unsupported platform");
        }
        return constructor.get();
    }
}
