package mod.chloeprime.aaaparticles.client.render.fabric;

import com.mojang.blaze3d.pipeline.RenderTarget;

@SuppressWarnings("unused")
public class RenderUtilImpl {
    public static int getDepthFormat(RenderTarget fb) {
        return 1;
    }

    public static void syncStencilState(RenderTarget from, RenderTarget to) {
    }
}
