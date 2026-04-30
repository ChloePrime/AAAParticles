package mod.chloeprime.aaaparticles.fabric;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAClientConfig;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.neoforged.fml.config.ModConfig;

public class AAAParticlesFabricClient extends AAAParticles implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NeoForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.CLIENT, AAAClientConfig.SPEC);
        AAAParticlesClient.init();
        AAAParticlesClient.setup();
        EffekRenderer.init();
    }
}