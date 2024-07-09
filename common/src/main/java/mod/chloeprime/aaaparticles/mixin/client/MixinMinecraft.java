
package mod.chloeprime.aaaparticles.mixin.client;

import com.mojang.blaze3d.platform.Window;
import mod.chloeprime.aaaparticles.client.Debug;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "resizeDisplay", at = @At("RETURN"))
    private void resizeCapturedDepthBuffer(CallbackInfo ci) {
        RenderStateCapture.CAPTURED_WORLD_DEPTH_BUFFER.resize(window.getWidth(), window.getHeight(), ON_OSX);
        RenderStateCapture.CAPTURED_HAND_DEPTH_BUFFER.resize(window.getWidth(), window.getHeight(), ON_OSX);
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V", shift = At.Shift.AFTER))
    private void emptyLeftClick(CallbackInfoReturnable<Boolean> cir) {
        Debug.leftClick();
    }

    @Shadow @Final private Window window;

    @Shadow @Final public static boolean ON_OSX;
}
