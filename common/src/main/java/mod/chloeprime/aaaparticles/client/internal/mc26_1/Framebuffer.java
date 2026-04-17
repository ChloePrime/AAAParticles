package mod.chloeprime.aaaparticles.client.internal.mc26_1;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.textures.GpuTexture;
import mod.chloeprime.aaaparticles.client.ClientPlatformMethods;
import mod.chloeprime.aaaparticles.client.render.RenderUtil;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.system.NativeType;

import static org.lwjgl.opengl.GL30C.*;

public class Framebuffer {
    private final RenderTarget target;
    public int frameBufferId = -1;
    public int width;
    public int height;

    public RenderTarget getRenderTarget() {
        return this.target;
    }

    public int getColorTextureId() {
        return getTextureId(target.getColorTexture());
    }

    public int getDepthTextureId() {
        return getTextureId(target.getDepthTexture());
    }

    public Framebuffer(@Nullable String label, int width, int height, boolean useDepth, boolean useStencil) {
        this(ClientPlatformMethods.get().newTextureTarget26_1(label, width, height, useDepth, useStencil));
    }

    public void resize(int width, int height, boolean ignoredOnOsx) {
        resize(width, height);
    }

    public void resize(int width, int height) {
        target.resize(width, height);
        RenderUtil.runFrameBufferCodeSafely(this::createFramebuffers);
    }

    Framebuffer(RenderTarget target) {
        this.target = target;
        RenderUtil.runFrameBufferCodeSafely(this::createFramebuffers);
    }

    private void destroyBuffers() {
    }

    private void createFramebuffers() {
        this.width = target.width;
        this.height = target.height;

        var tex2dBinding = glGetInteger(GL_TEXTURE_BINDING_2D);
        try {
            if (frameBufferId >= 0) {
                glDeleteFramebuffers(frameBufferId);
            }
            var fb = this.frameBufferId = GlStateManager.glGenFramebuffers();
            var useColor = true;
            glBindFramebuffer(GL_FRAMEBUFFER, fb);
            // noinspection ConstantValue
            if (useColor) {
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, target.getColorTexture());
            }
            if (target.useDepth) {
                var attachment = ClientPlatformMethods.get().isStencilEnabled26_1(target) ? GL_DEPTH_STENCIL_ATTACHMENT : GL_DEPTH_ATTACHMENT;
                glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, target.getDepthTexture());
            }
            checkFrameBuffer();
        } finally {
            glBindTexture(GL_TEXTURE_2D, tex2dBinding);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void glFramebufferTexture2D(@NativeType("GLenum") int target, @NativeType("GLenum") int attachment, @NativeType("GLenum") int texTarget, GpuTexture texture) {
        int texId = getTextureId(texture);
        glBindTexture(texTarget, texId);
        GL30C.glFramebufferTexture2D(target, attachment, texTarget, texId, 0);
    }

    private static int getTextureId(@Nullable GpuTexture texture) {
        if (texture == null) {
            return -1;
        }
        if (texture instanceof GlTexture glTexture) {
            return glTexture.glId();
        } else{
            throw new UnsupportedOperationException("Non-GL texture is not supported for AAAP's Framebuffer.");
        }
    }

    private void checkFrameBuffer() {
        var status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            throw new FramebufferException(status);
        }
    }
}
