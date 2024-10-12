package mod.chloeprime.aaaparticles.forge;

import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class AAAParticlesForgeClient extends AAAParticles {
    public static void onClientInit() {
    }

    @SuppressWarnings("unused")
    static void onClientSetup(FMLClientSetupEvent e) {
        AAAParticlesClient.setup();
    }
}
