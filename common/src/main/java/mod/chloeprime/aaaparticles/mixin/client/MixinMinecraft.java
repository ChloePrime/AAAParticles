package mod.chloeprime.aaaparticles.mixin.client;

import com.mojang.blaze3d.platform.Window;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "resizeDisplay", at = @At("RETURN"))
    private void resizeCapturedDepthBuffer(CallbackInfo ci) {
        RenderStateCapture.CAPTURED_WORLD_DEPTH_BUFFER.resize(window.getWidth(), window.getHeight(), ON_OSX);
        RenderStateCapture.CAPTURED_HAND_DEPTH_BUFFER.resize(window.getWidth(), window.getHeight(), ON_OSX);
    }

    @Shadow @Final private Window window;

    @Shadow @Final public static boolean ON_OSX;
}
