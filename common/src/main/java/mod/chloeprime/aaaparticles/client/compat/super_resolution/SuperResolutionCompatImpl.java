package mod.chloeprime.aaaparticles.client.compat.super_resolution;

import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import mod.chloeprime.aaaparticles.client.render.RenderUtil;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayDeque;
import java.util.Deque;

import static io.homo.superresolution.api.SuperResolutionAPI.*;
import static org.lwjgl.opengl.GL33C.*;

public class SuperResolutionCompatImpl {
    private static final Deque<Pair<Integer, Integer>> FRAMEBUFFER_MEM = new ArrayDeque<>();

    private static boolean isNotScaling() {
        return getRenderWidth() == getScreenWidth() && getRenderHeight() == getScreenHeight();
    }

    public static void preDrawEffeks() {
        if (isNotScaling()) {
            return;
        }
        var r = glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var w = glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        // Copy main -> SR foreground buffer
        var main = Minecraft.getInstance().getMainRenderTarget();
        var srFG = RenderStateCapture.SR_FOREGROUND;
        RenderUtil.copySafely(w, srFG.frameBufferId, main.width, main.height, main.width, main.height);
        RenderUtil.copyDepthSafely(w, srFG.frameBufferId, getRenderWidth(), getRenderHeight(), srFG.width, srFG.height);
        // Bind SR foreground buffer
        glBindFramebuffer(GL_READ_FRAMEBUFFER, srFG.frameBufferId);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, srFG.frameBufferId);
        FRAMEBUFFER_MEM.push(Pair.of(r, w));
    }

    public static void postDrawEffeks() {
        if (isNotScaling()) {
            return;
        }
        var fb = FRAMEBUFFER_MEM.poll();
        if (fb == null) {
            return;
        }
        int r = fb.getLeft();
        int w = fb.getRight();
        // Copy SR foreground buffer -> main
        var main = Minecraft.getInstance().getMainRenderTarget();
        var srFG = RenderStateCapture.SR_FOREGROUND;
        RenderUtil.copySafely(srFG.frameBufferId, w, srFG.width, srFG.height, main.width, main.height);
        // Recover FB binding
        glBindFramebuffer(GL_READ_FRAMEBUFFER, r);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, w);
    }
}
