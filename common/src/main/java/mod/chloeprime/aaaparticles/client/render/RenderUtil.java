package mod.chloeprime.aaaparticles.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL30.*;

public class RenderUtil {
    public static void copyDepthSafely(RenderTarget from, RenderTarget to) {
        var read = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var draw = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        to.copyDepthFrom(from);
        GL30.glBindFramebuffer(GL_READ_FRAMEBUFFER, read);
        GL30.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, draw);
    }

    public static void copyCurrentDepthTo(RenderTarget target) {
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
        GL30.glBindFramebuffer(GL_READ_FRAMEBUFFER, src);
        GL30.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, target.frameBufferId);
        GL30.glBlitFramebuffer(0, 0, srcWidth, srcHeight, 0, 0, target.width, target.height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        GL30.glBindFramebuffer(GL_READ_FRAMEBUFFER, readBackup);
        GL30.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawBackup);
    }

    public static void copyDepthSafely(RenderTarget src, int target, int targetWidth, int targetHeight) {
        var readBackup = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var drawBackup = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        GL30.glBindFramebuffer(GL_READ_FRAMEBUFFER, src.frameBufferId);
        GL30.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, target);
        GL30.glBlitFramebuffer(0, 0, src.width, src.height, 0, 0, targetWidth, targetHeight, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        GL30.glBindFramebuffer(GL_READ_FRAMEBUFFER, readBackup);
        GL30.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawBackup);
    }

    private static final Minecraft MC = Minecraft.getInstance();
}
