package mod.chloeprime.aaaparticles.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import mod.chloeprime.aaaparticles.common.util.LimitlessResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ResourceLocation.class, priority = Integer.MAX_VALUE)
public class MixinResourceLocation {
    @WrapMethod(method = "isValidPath")
    private static boolean effeksPathIsAlwaysValid(String path, Operation<Boolean> original) {
        if (path.startsWith("effeks/")) {
            return true;
        }
        return LimitlessResourceLocation.withPathCharValidationEnabled(() -> original.call(path));
    }

    @ModifyReturnValue(method = "validPathChar", at = @At("RETURN"))
    private static boolean validatePathCharOnlyIfNecessaryToBeCompatibleWithModernFix(boolean original) {
        return original || LimitlessResourceLocation.VALID_CHAR_STACK.get().getValue() == 0;
    }
}
