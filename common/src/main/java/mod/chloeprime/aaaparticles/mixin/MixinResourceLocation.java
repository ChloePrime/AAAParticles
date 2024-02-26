package mod.chloeprime.aaaparticles.mixin;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ResourceLocation.class, priority = Integer.MAX_VALUE - 5)
public class MixinResourceLocation {
    @Inject(method = "isValidPath", at = @At("HEAD"), cancellable = true)
    private static void isValidPath(String string, CallbackInfoReturnable<Boolean> cir) {
        if ("DUMMY".equals(string)) {
            cir.setReturnValue(false);
        }
        if (string.startsWith("effeks/")) {
            cir.setReturnValue(true);
        }
    }
}
