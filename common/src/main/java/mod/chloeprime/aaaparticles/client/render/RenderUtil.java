package mod.chloeprime.aaaparticles.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import mod.chloeprime.aaaparticles.client.ClientPlatformMethods;
import mod.chloeprime.aaaparticles.client.internal.ReloadTrackable;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import mod.chloeprime.aaaparticles.client.internal.mc26_1.Framebuffer;
import mod.chloeprime.aaaparticles.client.internal.mc26_1.FramebufferContainer;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL41C;

import java.util.Optional;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL41C.*;

public class RenderUtil {
    public static void transposeMatrix4x4(float[] m) {
        if (m.length != 16) {
            throw new IllegalArgumentException("Length of flattened 4x4 matrix \"m\" should be 16");
        }

        float m00, m01, m02, m03;
        float m10, m11, m12, m13;
        float m20, m21, m22, m23;
        float m30, m31, m32, m33;

        m00 = m[0];
        m01 = m[1];
        m02 = m[2];
        m03 = m[3];
        m10 = m[4];
        m11 = m[5];
        m12 = m[6];
        m13 = m[7];
        m20 = m[8];
        m21 = m[9];
        m22 = m[0xA];
        m23 = m[0xB];
        m30 = m[0xC];
        m31 = m[0xD];
        m32 = m[0xE];
        m33 = m[0xF];

        m[0] = m00;
        m[1] = m10;
        m[2] = m20;
        m[3] = m30;
        m[4] = m01;
        m[5] = m11;
        m[6] = m21;
        m[7] = m31;
        m[8] = m02;
        m[9] = m12;
        m[0xA] = m22;
        m[0xB] = m32;
        m[0xC] = m03;
        m[0xD] = m13;
        m[0xE] = m23;
        m[0xF] = m33;
    }

    public static void clearSamplerBindings(int start) {
        var max = glGetInteger(GL41C.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS);
        for (int i = start; i < max; i++) {
            glBindSampler(i, 0);
        }
    }

    public static void copyDepthSafely(RenderTarget from, RenderTarget to) {
        var read = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var draw = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        to.copyDepthFrom(from);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, read);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, draw);
    }

    public static void copyCurrentDepthTo(Framebuffer target) {
        var frameBuffer = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        var window = MC.getWindow();
        copyDepthSafely(frameBuffer, window.getWidth(), window.getHeight(), target);
    }

    public static void copyCurrentTo(Framebuffer target) {
        var frameBuffer = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        var window = MC.getWindow();
        copyDepthSafely(frameBuffer, window.getWidth(), window.getHeight(), target);
    }

    public static void pasteToCurrentDepthFrom(Framebuffer source) {
        var frameBuffer = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        var window = MC.getWindow();
        copyDepthSafely(source, frameBuffer, window.getWidth(), window.getHeight());
    }


    public static void copyDepthSafely(int src, int srcWidth, int srcHeight, Framebuffer target) {
        var readBackup = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var drawBackup = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, src);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, target.frameBufferId);
        glBlitFramebuffer(0, 0, srcWidth, srcHeight, 0, 0, target.width, target.height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, readBackup);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawBackup);
    }

    public static void copyDepthSafely(Framebuffer src, int target, int targetWidth, int targetHeight) {
        var readBackup = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var drawBackup = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, src.frameBufferId);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, target);
        glBlitFramebuffer(0, 0, src.width, src.height, 0, 0, targetWidth, targetHeight, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, readBackup);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawBackup);
    }

    public static void copySafely(Framebuffer src, Framebuffer target) {
        var readBackup = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var drawBackup = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, src.frameBufferId);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, target.frameBufferId);
        glBlitFramebuffer(0, 0, src.width, src.height, 0, 0, target.width, target.height, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, readBackup);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawBackup);
    }

    public static void runFrameBufferCodeSafely(Runnable code) {
        var readBackup = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var drawBackup = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        code.run();
        glBindFramebuffer(GL_READ_FRAMEBUFFER, readBackup);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawBackup);
    }

    public static void runEffekLoadCodeHealthily(Runnable code) {
        runPixelStoreCodeHealthily(() -> {
            var vao = glGetInteger(GL_VERTEX_ARRAY_BINDING);
            var vbo = glGetInteger(GL_ARRAY_BUFFER);
            var ibo = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
            code.run();
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        });
    }

    @ApiStatus.Internal
    public static void runPixelStoreCodeSafely(Runnable code) {
        var packAlignment = glGetInteger(GL_PACK_ALIGNMENT);
        var unpackRowLength = glGetInteger(GL_UNPACK_ROW_LENGTH);
        var unpackSkipRows = glGetInteger(GL_UNPACK_SKIP_ROWS);
        var unpackSkipPixels = glGetInteger(GL_UNPACK_SKIP_PIXELS);
        var unpackAlignment = glGetInteger(GL_UNPACK_ALIGNMENT);
        code.run();
        glPixelStorei(GL_PACK_ALIGNMENT, packAlignment);
        glPixelStorei(GL_UNPACK_ROW_LENGTH, unpackRowLength);
        glPixelStorei(GL_UNPACK_SKIP_ROWS, unpackSkipRows);
        glPixelStorei(GL_UNPACK_SKIP_PIXELS, unpackSkipPixels);
        glPixelStorei(GL_UNPACK_ALIGNMENT, unpackAlignment);
    }

    public static void runPixelStoreCodeHealthily(Runnable code) {
        runPixelStoreCodeSafely(() -> {
            glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
            glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
            glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
            code.run();
        });
    }

    public static <T> T supplyPixelStoreCodeHealthily(Supplier<T> code) {
        var ret = new MutableObject<T>();
        runPixelStoreCodeHealthily(() -> ret.setValue(code.get()));
        return ret.getValue();
    }

    public static <T> T supplyEffekLoadCodeHealthily(Supplier<T> code) {
        var ret = new MutableObject<T>();
        runEffekLoadCodeHealthily(() -> ret.setValue(code.get()));
        return ret.getValue();
    }

    public static boolean isReloadingResourcePacks() {
        return ((ReloadTrackable) MC).aaa_particles$isReloading();
    }

    public static void refreshBackgroundFrameBuffer() {
        refreshFrameBuffer(RenderStateCapture.DISTORTION_BACKGROUND);
    }

    public static Optional<Framebuffer> prepareBackgroundBuffer() {
        if (isReloadingResourcePacks()) {
            return Optional.empty();
        }
        var background = RenderStateCapture.DISTORTION_BACKGROUND;
        try (var fbc = new FramebufferContainer(MC.getMainRenderTarget())) {
            RenderUtil.copySafely(fbc.framebuffer(), background);
        }
        return Optional.of(background);
    }

    public static int getDepthFormat(Framebuffer fb) {
        return ClientPlatformMethods.get().getDepthFormat(fb.getRenderTarget());
    }

    @SuppressWarnings("SameParameterValue")
    private static void refreshFrameBuffer(Framebuffer fb) {
        RenderUtil.runFrameBufferCodeSafely(() -> fb.resize(fb.width, fb.height));
    }

    public static final Minecraft MC = Minecraft.getInstance();
}
