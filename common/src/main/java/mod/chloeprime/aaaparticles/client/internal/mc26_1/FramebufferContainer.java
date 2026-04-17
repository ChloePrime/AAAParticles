package mod.chloeprime.aaaparticles.client.internal.mc26_1;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.apache.commons.lang3.mutable.MutableObject;
import org.lwjgl.opengl.GL30C;

import java.io.Closeable;
import java.util.Objects;

public record FramebufferContainer(MutableObject<Framebuffer> fb) implements Closeable {
    public FramebufferContainer(RenderTarget target) {
        this(new MutableObject<>(new Framebuffer(target)));
    }

    public Framebuffer framebuffer() {
        return Objects.requireNonNull(fb.get(), "Trying to access a closed FramebufferContainer");
    }

    @Override
    public void close() {
        var fb = this.fb.get();
        if (fb != null) {
            if (fb.frameBufferId >= 0) {
                GL30C.glDeleteFramebuffers(fb.frameBufferId);
                fb.frameBufferId = -1;
            }
            this.fb.setValue(null);
        }
    }
}
