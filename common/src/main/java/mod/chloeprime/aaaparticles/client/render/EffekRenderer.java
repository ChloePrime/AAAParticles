package mod.chloeprime.aaaparticles.client.render;

import static mod.chloeprime.aaaparticles.client.render.EffekRenderer.MinecraftHolder.MINECRAFT;

import java.nio.FloatBuffer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.BufferUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import mod.chloeprime.aaaparticles.api.client.effekseer.DeviceType;
import mod.chloeprime.aaaparticles.api.client.effekseer.Effekseer;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.client.loader.EffekAssetLoader;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionHand;

/**
 * @author ChloePrime
 */
public class EffekRenderer {
    private static final FloatBuffer CAMERA_TRANSFORM_BUFFER = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer PROJECTION_BUFFER = BufferUtils.createFloatBuffer(16);
    private static final AtomicBoolean INIT = new AtomicBoolean();

    public static void init() {
        if (INIT.compareAndExchange(false, true)) {
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

    public static void onRenderWorldLast(float partialTick, PoseStack pose, Matrix4f projection, Camera camera) {
        draw(ParticleEmitter.Type.WORLD, partialTick, pose, projection, camera);
    }

    public static void onRenderHand(float partialTick, InteractionHand hand, PoseStack pose, Matrix4f projection, Camera camera) {
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

        projection.store(PROJECTION_BUFFER);
        transposeMatrix(PROJECTION_BUFFER);
        PROJECTION_BUFFER.get(PROJECTION_MATRIX_DATA);

        pose.pushPose();
        {
            if (type == ParticleEmitter.Type.WORLD) {
                pose.translate(-camera.getPosition().x(), -camera.getPosition().y(), -camera.getPosition().z());
            }

            pose.last().pose().store(CAMERA_TRANSFORM_BUFFER);
            transposeMatrix(CAMERA_TRANSFORM_BUFFER);
            CAMERA_TRANSFORM_BUFFER.get(CAMERA_TRANSFORM_DATA);
        }
        pose.popPose();

        Optional.ofNullable(MINECRAFT.levelRenderer.getParticlesTarget())
                .ifPresent(rt -> rt.copyDepthFrom(MINECRAFT.getMainRenderTarget()));

        float deltaFrames = 60 * getDeltaTime(type);

        RenderType.PARTICLES_TARGET.setupRenderState();
        EffekAssetLoader.get().forEach((id, inst) -> inst.draw(type, w, h, CAMERA_TRANSFORM_DATA, PROJECTION_MATRIX_DATA, deltaFrames, partialTick));
        RenderType.PARTICLES_TARGET.clearRenderState();

        CAMERA_TRANSFORM_BUFFER.clear();
        PROJECTION_BUFFER.clear();
    }

    private static void transposeMatrix(FloatBuffer m) {
        float m00, m01, m02, m03;
        float m10, m11, m12, m13;
        float m20, m21, m22, m23;
        float m30, m31, m32, m33;

        m00 = m.get(0);
        m01 = m.get(1);
        m02 = m.get(2);
        m03 = m.get(3);
        m10 = m.get(4);
        m11 = m.get(5);
        m12 = m.get(6);
        m13 = m.get(7);
        m20 = m.get(8);
        m21 = m.get(9);
        m22 = m.get(0xA);
        m23 = m.get(0xB);
        m30 = m.get(0xC);
        m31 = m.get(0xD);
        m32 = m.get(0xE);
        m33 = m.get(0xF);

        m.put(0 , m00);
        m.put(1 , m10);
        m.put(2 , m20);
        m.put(3 , m30);
        m.put(4 , m01);
        m.put(5 , m11);
        m.put(6 , m21);
        m.put(7 , m31);
        m.put(8 , m02);
        m.put(9 , m12);
        m.put(0xA , m22);
        m.put(0xB , m32);
        m.put(0xC , m03);
        m.put(0xD , m13);
        m.put(0xE , m23);
        m.put(0xF , m33);
    }

    private static final long[] lastDrawTimeByNanos = new long[256];

    private static float getDeltaTime(ParticleEmitter.Type type) {
        long last = lastDrawTimeByNanos[type.ordinal()];
        if (last == 0) {
            lastDrawTimeByNanos[type.ordinal()] = System.nanoTime();
            return 1f / 60;
        }

        long now = System.nanoTime();
        lastDrawTimeByNanos[type.ordinal()] = now;
        return (float) ((now - last) * 1e-9);
    }

    public static final class MinecraftHolder {
        public static final Minecraft MINECRAFT = Minecraft.getInstance();
    }
}
