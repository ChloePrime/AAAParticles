package mod.chloeprime.aaaparticles.mixin.client.mc26_1;

import mod.chloeprime.aaaparticles.client.internal.mc26_1.EnhancedCameraRenderState;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class MixinCamera {
    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void setCameraInfo(CameraRenderState cameraState, float cameraEntityPartialTicks, CallbackInfo ci) {
        var self = (Camera) (Object) this;
        ((EnhancedCameraRenderState) cameraState).aaa_particles$getCamera().setValue(self);
    }
}
