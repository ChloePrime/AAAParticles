package mod.chloeprime.aaaparticles.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.api.client.EffectRegistry;
import mod.chloeprime.aaaparticles.api.client.effekseer.DeviceType;
import mod.chloeprime.aaaparticles.api.client.effekseer.Effekseer;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import mod.chloeprime.aaaparticles.client.internal.EffekFpvRenderer;
import mod.chloeprime.aaaparticles.client.internal.RenderContext;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import mod.chloeprime.aaaparticles.client.util.GlDebug;
import mod.chloeprime.aaaparticles.client.util.GlDebugIds;
import mod.chloeprime.aaaparticles.client.util.RenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import org.joml.Matrix4f;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static mod.chloeprime.aaaparticles.client.render.EffekRenderer.MinecraftHolder.MINECRAFT;
import static mod.chloeprime.aaaparticles.client.render.RenderUtil.pasteToCurrentDepthFrom;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author ChloePrime
 */
public class EffekRenderer {
    private static final AtomicBoolean INIT = new AtomicBoolean();

    public static void init() {
        if (INIT.compareAndExchange(false, true)) {
            return;
        }
        if (NativePlatform.isRunningOnUnsupportedPlatform()) {
            AAAParticles.LOGGER.warn("AAAParticles is running un unsupported platform {}, this mod will be no-op!", NativePlatform.current());
            return;
        }
        if (Effekseer.getDeviceType() != DeviceType.OPENGL) {
            if (!Effekseer.init()) {
                throw new ExceptionInInitializerError("Failed to initialize Effekseer");
            }
            Runtime.getRuntime().addShutdownHook(
                    new Thread(Effekseer::terminate, "ShutdownHook Effekseer::terminate")
            );
        }
    }

    public static void renderWorldEffeks(DeltaTracker deltaTracker, boolean renderHand, ItemInHandRenderer itemInHandRenderer) {
        glDepthMask(true);
        glDepthFunc(GL_LEQUAL);

        var partial = deltaTracker.getGameTimeDeltaPartialTick(false);

        if (RenderContext.renderLevelDeferred() && RenderStateCapture.LEVEL.hasCapture) {
            RenderStateCapture.LEVEL.hasCapture = false;

            pasteToCurrentDepthFrom(RenderStateCapture.CAPTURED_WORLD_DEPTH_BUFFER);
            EffekRenderer.renderWorldEffeks(partial, RenderStateCapture.LEVEL.pose, RenderStateCapture.LEVEL.projection, RenderStateCapture.LEVEL.camera);
        }
        if (RenderContext.renderHandDeferred() && renderHand) {
            if (RenderContext.captureHandDepth()) {
                pasteToCurrentDepthFrom(RenderStateCapture.CAPTURED_HAND_DEPTH_BUFFER);
            }
            ((EffekFpvRenderer) itemInHandRenderer).aaaParticles$renderFpvEffek(partial, MINECRAFT.player);
        }
    }

    public static void renderWorldEffeks(float partialTick, PoseStack pose, Matrix4f projection, Camera camera) {
        if (NativePlatform.isRunningOnUnsupportedPlatform()) {
            return;
        }
        draw(ParticleEmitter.Type.WORLD, partialTick, pose, projection, camera);
    }

    public static void onRenderHand(float partialTick, InteractionHand hand, PoseStack pose, Matrix4f projection, Camera camera) {
        if (NativePlatform.isRunningOnUnsupportedPlatform()) {
            return;
        }
        var type = switch (hand) {
            case MAIN_HAND -> ParticleEmitter.Type.FIRST_PERSON_MAINHAND;
            case OFF_HAND -> ParticleEmitter.Type.FIRST_PERSON_OFFHAND;
        };
        draw(type, partialTick, pose, projection, camera);
    }

    private static final float[] CAMERA_TRANSFORM_DATA = new float[16];
    private static final float[] PROJECTION_MATRIX_DATA = new float[16];

    private static void draw(ParticleEmitter.Type type, float partialTick, PoseStack pose, Matrix4f projection, Camera camera) {
        int w = MINECRAFT.getWindow().getWidth();
        int h = MINECRAFT.getWindow().getHeight();

        projection.get(PROJECTION_MATRIX_DATA);
        transposeMatrix(PROJECTION_MATRIX_DATA);

        pose.pushPose();
        {
            if (type == ParticleEmitter.Type.WORLD) {
                pose.translate(-camera.getPosition().x(), -camera.getPosition().y(), -camera.getPosition().z());
            }

            pose.last().pose().get(CAMERA_TRANSFORM_DATA);
            transposeMatrix(CAMERA_TRANSFORM_DATA);
        }
        pose.popPose();

        Optional.ofNullable(MINECRAFT.levelRenderer.getParticlesTarget())
                .ifPresent(rt -> RenderUtil.copyDepthSafely(MINECRAFT.getMainRenderTarget(), rt));

        float deltaFrames = 60 * mod.chloeprime.aaaparticles.common.util.DeltaTracker.getDeltaTime();
        float realDelta = MINECRAFT.isPaused() ? 0 : deltaFrames;

        RenderTypes.particleTarget().setupRenderState();

        RenderUtil.runPixelStoreCodeSafely(() -> {
            GlDebug.pushDebugGroup(GlDebugIds.EFFEK_RENDER_DISPATCH, () -> "[AAAParticle] Rendering Effeks");
            var background = RenderUtil.prepareBackgroundBuffer().orElse(null);
            EffectRegistry.forEach((id, lazy) -> lazy.lazyGet().ifPresent(def -> def.draw(
                    type,
                    camera.getLookVector(), camera.getPosition().toVector3f(),
                    w, h,
                    CAMERA_TRANSFORM_DATA, PROJECTION_MATRIX_DATA,
                    realDelta, partialTick, background
            )));
            RenderUtil.clearSamplerBindings(5);
            GlDebug.popDebugGroup();
        });

        RenderTypes.particleTarget().clearRenderState();
    }

    private static void transposeMatrix(float[] m) {
        RenderUtil.transposeMatrix4x4(m);
    }

    public static final class MinecraftHolder {
        public static final Minecraft MINECRAFT = Minecraft.getInstance();
    }
}
