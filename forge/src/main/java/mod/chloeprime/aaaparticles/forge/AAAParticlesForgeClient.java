package mod.chloeprime.aaaparticles.forge;

import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class AAAParticlesForgeClient {
    static void onRenderStage(RenderLevelStageEvent e) {
        if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            EffekRenderer.onRenderWorldLast(e.getPartialTick(), e.getPoseStack(), e.getProjectionMatrix());
        }
    }

    static void onClientSetup(FMLClientSetupEvent e) {
        AAAParticlesClient.setup();
    }
}
