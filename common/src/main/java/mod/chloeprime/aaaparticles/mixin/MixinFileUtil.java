package mod.chloeprime.aaaparticles.mixin;

import net.minecraft.FileUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FileUtil.class, priority = Integer.MAX_VALUE)
public class MixinFileUtil {
    @Inject(method = "isValidStrictPathSegment", at = @At("HEAD"), cancellable = true)
    private static void disableStrictPathSegmentValidation(String string, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!"DUMMY".equals(string));
    }
}
