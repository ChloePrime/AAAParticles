package mod.chloeprime.aaaparticles.forge;

import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class AAAParticlesForgeClient {
    public static final boolean USE_RENDER_EVENT = false;
    public static final RenderLevelStageEvent.Stage RENDER_STAGE = RenderLevelStageEvent.Stage.AFTER_LEVEL;

    static void onRenderStage(RenderLevelStageEvent e) {
        if (e.getStage() != RENDER_STAGE) {
            return;
        }
        EffekRenderer.onRenderWorldLast(e.getPartialTick(), e.getPoseStack(), e.getProjectionMatrix(), e.getCamera());
    }

    static void onClientSetup(FMLClientSetupEvent e) {
        AAAParticlesClient.setup();
    }
}
