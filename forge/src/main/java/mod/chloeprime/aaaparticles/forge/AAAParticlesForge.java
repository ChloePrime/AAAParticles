package mod.chloeprime.aaaparticles.forge;

import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforgespi.Environment;

@Mod(AAAParticles.MOD_ID)
public class AAAParticlesForge extends AAAParticles  {
    public AAAParticlesForge(IEventBus modbus, ModContainer container) {
        AAAParticles.init();

        if (Environment.get().getDist().isClient()) {
            AAAParticlesForgeClient.onClientInit(container);
            AAAParticlesClient.init();
            modbus.addListener(AAAParticlesForgeClient::onClientSetup);
        }
    }
}