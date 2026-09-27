package mod.chloeprime.aaaparticles.common.util;

import mod.chloeprime.aaaparticles.PlatformMethods;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @see mod.chloeprime.aaaparticles.mixin.MixinResourceLocation
 */
public final class LimitlessResourceLocation  {
    private static final AtomicInteger MODERN_FIX_INSTALLED = new AtomicInteger(-1);

    private static final ClassValue<Boolean> IS_CLASS_FROM_MODERN_FIX = new ClassValue<>() {
        @Override
        protected Boolean computeValue(@Nonnull Class<?> type) {
            return type.getName().startsWith("org.embeddedt.modernfix");
        }
    };

    public static boolean isModernFixInstalled() {
        var existing = MODERN_FIX_INSTALLED.get();
        if (existing >= 0) {
            return existing > 0;
        }
        return MODERN_FIX_INSTALLED.updateAndGet(before -> {
            if (before >= 0) {
                return before;
            }
            return PlatformMethods.get().isModLoaded("modernfix");
        }) > 0;
    }

    public static boolean isModernFixClass(@Nullable Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return IS_CLASS_FROM_MODERN_FIX.get(clazz);
    }

    private LimitlessResourceLocation() {
    }
}
