package mod.chloeprime.aaaparticles.client.internal.mc26_1;

import org.lwjgl.opengl.GL30C;

public class FramebufferException extends RuntimeException {
    public FramebufferException(int code) {
        var text = switch (code) {
            case GL30C.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> "Incomplete Attachment";
            case GL30C.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT -> "Missing Attachment";
            case GL30C.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER -> "Incomplete Read Buffer";
            case GL30C.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER -> "Incomplete Draw Buffer";
            case GL30C.GL_FRAMEBUFFER_UNSUPPORTED -> "Unsupported Framebuffer";
            case GL30C.GL_OUT_OF_MEMORY -> "Out of memory";
            default -> "Unknown framebuffer error: %d".formatted(code);
        };
        super(text);
    }
}
