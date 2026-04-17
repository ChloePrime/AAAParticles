package mod.chloeprime.aaaparticles.mixin.client;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import mod.chloeprime.aaaparticles.client.internal.RenderContext;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import mod.chloeprime.aaaparticles.client.internal.mc26_1.EnhancedCameraRenderState;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static mod.chloeprime.aaaparticles.client.render.RenderUtil.copyCurrentDepthTo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void onRenderLevelLast(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker, boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix, GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky, ChunkSectionsToRender chunkSectionsToRender, CallbackInfo ci) {
        var camera = EnhancedCameraRenderState.getCamera(cameraState);
        var capture = RenderStateCapture.LEVEL;
        capture.pose.setIdentity();
        capture.pose.mulPose(cameraState.viewRotationMatrix);
        capture.projection.set(cameraState.projectionMatrix);
        capture.camera = camera;
        capture.cameraState_26_1 = cameraState;
        capture.hasCapture = true;

        if (RenderContext.renderLevelDeferred()) {
            copyCurrentDepthTo(RenderStateCapture.CAPTURED_WORLD_DEPTH_BUFFER);
        } else {
            var partial = deltaTracker.getGameTimeDeltaPartialTick(false);
            EffekRenderer.renderWorldEffeks(partial, capture.pose, capture.projection, capture.camera, RenderStateCapture.LEVEL.cameraState_26_1);
        }
    }
}
