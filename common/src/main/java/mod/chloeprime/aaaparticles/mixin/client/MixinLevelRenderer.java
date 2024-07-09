package mod.chloeprime.aaaparticles.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.aaaparticles.client.internal.RenderContext;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static mod.chloeprime.aaaparticles.client.render.RenderUtil.copyCurrentDepthTo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void onRenderLevelLast(PoseStack poseStack, float partial, long l, boolean bl, Camera camera, GameRenderer arg3, LightTexture arg4, Matrix4f projection, CallbackInfo ci) {
        var capture = RenderStateCapture.LEVEL;
        var currentPose = poseStack.last();
        var capturedPose = capture.pose.last();
        capturedPose.pose().set(currentPose.pose());
        capturedPose.normal().set(currentPose.normal());
        capture.projection.set(projection);
        capture.camera = camera;
        capture.hasCapture = true;

        if (RenderContext.renderLevelDeferred()) {
            copyCurrentDepthTo(RenderStateCapture.CAPTURED_WORLD_DEPTH_BUFFER);
        } else {
            EffekRenderer.onRenderWorldLast(partial, capture.pose, capture.projection, capture.camera);
        }
    }
}
