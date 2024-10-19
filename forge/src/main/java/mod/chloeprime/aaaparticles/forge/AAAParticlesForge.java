package mod.chloeprime.aaaparticles.forge;

import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;

@Mod(AAAParticles.MOD_ID)
public class AAAParticlesForge extends AAAParticles  {
    public AAAParticlesForge() {
        var modbus = ModLoadingContext.get().getActiveContainer().getEventBus();
        AAAParticles.init();

        if (net.neoforged.neoforgespi.Environment.get().getDist().isClient()) {
            AAAParticlesForgeClient.onClientInit();
            AAAParticlesClient.init();
            modbus.addListener(AAAParticlesForgeClient::onClientSetup);
        }
    }
}