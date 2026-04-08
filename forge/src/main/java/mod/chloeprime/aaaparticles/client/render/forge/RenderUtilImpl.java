package mod.chloeprime.aaaparticles.client.render.forge;

import com.mojang.blaze3d.pipeline.RenderTarget;

@SuppressWarnings("unused")
public class RenderUtilImpl {
    public static int getDepthFormat(RenderTarget fb) {
        return fb.isStencilEnabled() ? 2 : 1;
    }

    public static void syncStencilState(RenderTarget from, RenderTarget to) {
        if (from.isStencilEnabled() && !to.isStencilEnabled()) {
            to.enableStencil();
        }
    }
}
