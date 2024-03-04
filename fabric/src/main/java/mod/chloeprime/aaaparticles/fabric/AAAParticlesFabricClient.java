package mod.chloeprime.aaaparticles.fabric;

import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.fabricmc.api.ClientModInitializer;

public class AAAParticlesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AAAParticlesClient.init();
        AAAParticlesClient.setup();
        EffekRenderer.init();
    }
}