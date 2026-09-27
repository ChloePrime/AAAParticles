package mod.chloeprime.aaaparticles.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import mod.chloeprime.aaaparticles.common.util.LimitlessResourceLocation;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Identifier.class, priority = Integer.MAX_VALUE)
public class MixinResourceLocation {
    private static final @Unique StackWalker aaa_particles$STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    @WrapMethod(method = "isValidPath")
    private static boolean effeksPathIsAlwaysValid(String path, Operation<Boolean> original) {
        if (path.startsWith("effeks/")) {
            return true;
        }
        return original.call(path);
    }

    @ModifyReturnValue(method = "validPathChar", at = @At("RETURN"))
    private static boolean modernfixCompat(boolean original) {
        return original || (LimitlessResourceLocation.isModernFixInstalled() && LimitlessResourceLocation.isModernFixClass(aaa_particles$STACK_WALKER.walk(frames -> frames
                .dropWhile(cl -> cl.getDeclaringClass() == Identifier.class)
                .findFirst()
                .map(StackWalker.StackFrame::getDeclaringClass)
                .orElse(null))));
    }
}
