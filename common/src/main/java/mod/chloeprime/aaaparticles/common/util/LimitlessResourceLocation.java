package mod.chloeprime.aaaparticles.common.util;

import mod.chloeprime.aaaparticles.PlatformMethods;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @see mod.chloeprime.aaaparticles.mixin.MixinResourceLocation
 */
public final class LimitlessResourceLocation  {
    public static final class IsModLoadedHolder {
        public static boolean MODERN_FIX_INSTALLED = PlatformMethods.get().isModLoaded("modernfix");
    }

    private static final ClassValue<Boolean> IS_CLASS_FROM_MODERN_FIX = new ClassValue<>() {
        @Override
        protected Boolean computeValue(@Nonnull Class<?> type) {
            return type.getName().startsWith("org.embeddedt.modernfix");
        }
    };

    public static boolean isModernFixClass(@Nullable Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return IS_CLASS_FROM_MODERN_FIX.get(clazz);
    }

    private LimitlessResourceLocation() {
    }
}
