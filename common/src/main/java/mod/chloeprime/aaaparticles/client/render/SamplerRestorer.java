package mod.chloeprime.aaaparticles.client.render;

import com.mojang.blaze3d.systems.RenderSystem;

import static org.lwjgl.opengl.GL41C.*;

public final class SamplerRestorer {
    static {
        RenderSystem.assertOnRenderThread();
    }

    private static final int MAX_TEX_UNITS = Math.min(8, glGetInteger(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS));
    private static final int[] SAMPLERS = new int[MAX_TEX_UNITS];

    public static void recordSamplers() {
        int currentActiveTexture = glGetInteger(GL_ACTIVE_TEXTURE);
        for (int i = 0; i < MAX_TEX_UNITS; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            SAMPLERS[i] = glGetInteger(GL_SAMPLER_BINDING);
        }
        glActiveTexture(currentActiveTexture);
    }

    public static void recoverSamplers() {
        int currentActiveTexture = glGetInteger(GL_ACTIVE_TEXTURE);
        for (int i = 0; i < MAX_TEX_UNITS; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            if (SAMPLERS[i] != glGetInteger(GL_SAMPLER_BINDING)) {
                glBindSampler(i, SAMPLERS[i]);
            }
        }
        glActiveTexture(currentActiveTexture);
    }

    private SamplerRestorer() {
    }
}
