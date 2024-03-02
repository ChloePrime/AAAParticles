package mod.chloeprime.aaaparticles.forge;

import dev.architectury.platform.forge.EventBuses;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.Environment;

@Mod(AAAParticles.MOD_ID)
public class AAAParticlesForge {
    public AAAParticlesForge() {
        var modbus = FMLJavaModLoadingContext.get().getModEventBus();
        var forgebus = MinecraftForge.EVENT_BUS;
        EventBuses.registerModEventBus(AAAParticles.MOD_ID, modbus);
        AAAParticles.init();

        if (Environment.get().getDist().isClient()) {
            AAAParticlesClient.init();
            modbus.addListener(AAAParticlesForgeClient::onClientSetup);
            if (AAAParticlesForgeClient.USE_RENDER_EVENT) {
                forgebus.addListener(AAAParticlesForgeClient::onRenderStage);
            }
        }
    }
}