package mod.chloeprime.aaaparticles.fabric;

import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.client.ModClientConfig;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.api.ModLoadingContext;

public class AAAParticlesFabricClient extends AAAParticles implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModLoadingContext.registerConfig(MOD_ID, ModConfig.Type.CLIENT, ModClientConfig.SPEC);
        AAAParticlesClient.init();
        AAAParticlesClient.setup();
        EffekRenderer.init();
    }
}