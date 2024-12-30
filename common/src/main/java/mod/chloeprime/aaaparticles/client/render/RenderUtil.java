package mod.chloeprime.aaaparticles.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;
import mod.chloeprime.aaaparticles.client.internal.ReloadTrackable;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL11;

import java.util.Optional;

import static org.lwjgl.opengl.GL30.*;

public class RenderUtil {
    public static void copyDepthSafely(RenderTarget from, RenderTarget to) {
        var read = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var draw = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        to.copyDepthFrom(from);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, read);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, draw);
    }

    public static void copyCurrentDepthTo(RenderTarget target) {
        var frameBuffer = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        var window = MC.getWindow();
        copyDepthSafely(frameBuffer, window.getWidth(), window.getHeight(), target);
    }

    public static void copyCurrentTo(RenderTarget target) {
        var frameBuffer = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        var window = MC.getWindow();
        copyDepthSafely(frameBuffer, window.getWidth(), window.getHeight(), target);
    }

    public static void pasteToCurrentDepthFrom(RenderTarget source) {
        var frameBuffer = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        var window = MC.getWindow();
        copyDepthSafely(source, frameBuffer, window.getWidth(), window.getHeight());
    }


    public static void copyDepthSafely(int src, int srcWidth, int srcHeight, RenderTarget target) {
        var readBackup = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var drawBackup = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, src);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, target.frameBufferId);
        glBlitFramebuffer(0, 0, srcWidth, srcHeight, 0, 0, target.width, target.height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, readBackup);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawBackup);
    }

    public static void copyDepthSafely(RenderTarget src, int target, int targetWidth, int targetHeight) {
        var readBackup = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var drawBackup = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, src.frameBufferId);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, target);
        glBlitFramebuffer(0, 0, src.width, src.height, 0, 0, targetWidth, targetHeight, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, readBackup);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawBackup);
    }

    public static void copySafely(RenderTarget src, RenderTarget target) {
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
        var texture = GL11.glGetInteger(GL_TEXTURE_BINDING_2D);
        code.run();
        glBindFramebuffer(GL_READ_FRAMEBUFFER, readBackup);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawBackup);
        glBindTexture(GL_TEXTURE_2D, texture);
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

    public static boolean isReloadingResourcePacks() {
        return ((ReloadTrackable) MC).aaa_particles$isReloading();
    }

    public static void refreshBackgroundFrameBuffer() {
        refreshFrameBuffer(RenderStateCapture.DISTORTION_BACKGROUND);
    }

    public static Optional<RenderTarget> prepareBackgroundBuffer() {
        if (isReloadingResourcePacks()) {
            return Optional.empty();
        }
        var background = RenderStateCapture.DISTORTION_BACKGROUND;
        syncStencilState(MC.getMainRenderTarget(), background);
        RenderUtil.copySafely(MC.getMainRenderTarget(), background);
        return Optional.of(background);
    }

    @ExpectPlatform
    @SuppressWarnings("unused")
    public static void syncStencilState(RenderTarget from, RenderTarget to) {
        throw new AbstractMethodError();
    }

    @SuppressWarnings("SameParameterValue")
    private static void refreshFrameBuffer(RenderTarget fb) {
        RenderUtil.runPixelStoreCodeHealthily(() -> fb.resize(fb.width, fb.height, Minecraft.ON_OSX));
    }

    public static final Minecraft MC = Minecraft.getInstance();
}
