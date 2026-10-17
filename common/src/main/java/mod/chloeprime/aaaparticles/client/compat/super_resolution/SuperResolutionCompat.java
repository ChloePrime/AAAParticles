package mod.chloeprime.aaaparticles.client.compat.super_resolution;

import mod.chloeprime.aaaparticles.PlatformMethods;

public class SuperResolutionCompat {
    public static final boolean TARGET_INSTALLED = PlatformMethods.get().isModLoadedFailFast("super_resolution");

    public static void preDrawEffeks() {
        if (TARGET_INSTALLED) {
            SuperResolutionCompatImpl.preDrawEffeks();
        }
    }

    public static void postDrawEffeks() {
        if (TARGET_INSTALLED) {
            SuperResolutionCompatImpl.postDrawEffeks();
        }
    }
}
