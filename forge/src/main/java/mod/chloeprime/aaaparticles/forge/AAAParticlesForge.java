package mod.chloeprime.aaaparticles.forge;

import dev.architectury.platform.forge.EventBuses;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.Environment;

@Mod(AAAParticles.MOD_ID)
public class AAAParticlesForge extends AAAParticles {
    public AAAParticlesForge() {
        var modbus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(MOD_ID, modbus);
        AAAParticles.init();

        if (Environment.get().getDist().isClient()) {
            AAAParticlesForgeClient.onClientInit();
            AAAParticlesClient.init();
            modbus.addListener(AAAParticlesForgeClient::onClientSetup);
        }
    }
}