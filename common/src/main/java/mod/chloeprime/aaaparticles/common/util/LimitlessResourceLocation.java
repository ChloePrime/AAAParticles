package mod.chloeprime.aaaparticles.common.util;

import org.apache.commons.lang3.mutable.MutableInt;

import java.util.concurrent.Callable;

/**
 * @see mod.chloeprime.aaaparticles.mixin.MixinResourceLocation
 */
public final class LimitlessResourceLocation  {
    public static final ThreadLocal<MutableInt> VALID_CHAR_STACK = ThreadLocal.withInitial(MutableInt::new);

    public static <R> R withPathCharValidationEnabled(Callable<R> code) {
        var stack = VALID_CHAR_STACK.get();
        try {
            stack.increment();
            return code.call();
        } catch (Exception ex) {
            throw ex instanceof RuntimeException re ? re : new RuntimeException(ex);
        } finally {
            stack.decrement();
        }
    }

    private LimitlessResourceLocation() {
    }
}
