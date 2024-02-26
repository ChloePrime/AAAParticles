package mod.chloeprime.aaaparticles.forge;

import dev.architectury.platform.forge.EventBuses;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AAAParticles.MOD_ID)
public class AAAParticlesForge {
    public AAAParticlesForge() {
        var modbus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(AAAParticles.MOD_ID, modbus);
        AAAParticles.init();

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> AAAParticlesClient::init);
        modbus.register(this);

        MinecraftForge.EVENT_BUS.addListener(this::onRenderStage);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent e) {
        AAAParticlesClient.setup();
    }

    private void onRenderStage(RenderLevelStageEvent e) {
        if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            EffekRenderer.onRenderWorldLast(e.getPartialTick(), e.getPoseStack(), e.getProjectionMatrix());
        }
    }
}