package mod.chloeprime.aaaparticles.mixin.client;

import com.mojang.blaze3d.platform.GlDebug;
import mod.chloeprime.aaaparticles.client.util.GlErrorSilencer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlDebug.class)
public class MixinGlDebug {
    @Inject(method = "printDebugLog", at = @At("HEAD"), cancellable = true)
    private static void preventLogSpam(int i, int j, int k, int l, int m, long n, long o, CallbackInfo ci) {
        GlErrorSilencer.trySilence(k, ci::cancel);
    }
}
