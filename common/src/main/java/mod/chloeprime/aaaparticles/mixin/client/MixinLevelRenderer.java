package mod.chloeprime.aaaparticles.mixin.client;

import static mod.chloeprime.aaaparticles.client.render.RenderUtil.copyCurrentDepthTo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import mod.chloeprime.aaaparticles.client.internal.RenderContext;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void onRenderLevelLast(PoseStack poseStack, float partial, long l, boolean bl, Camera camera, GameRenderer arg3, LightTexture arg4, Matrix4f projection, CallbackInfo ci) {
        var capture = RenderStateCapture.LEVEL;
        var currentPose = poseStack.last();
        var capturedPose = capture.pose.last();
        capturedPose.pose().load(currentPose.pose());
        capturedPose.normal().load(currentPose.normal());
        capture.projection.load(projection);
        capture.camera = camera;
        capture.hasCapture = true;

        if (RenderContext.renderLevelDeferred()) {
            copyCurrentDepthTo(RenderStateCapture.CAPTURED_WORLD_DEPTH_BUFFER);
        } else {
            EffekRenderer.onRenderWorldLast(partial, capture.pose, capture.projection, capture.camera);
        }
    }
}
