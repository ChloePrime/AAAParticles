package mod.chloeprime.aaaparticles.fabric;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAClientConfig;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import mod.chloeprime.aaaparticles.client.loader.EffekAssetLoader;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.config.ModConfig;

public class AAAParticlesFabricClient extends AAAParticles implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.CLIENT, AAAClientConfig.SPEC);
        AAAParticlesClient.init();
        AAAParticlesClient.setup();
        EffekRenderer.init();
        if (!NativePlatform.isDataGen()) {
            ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(AAAParticles.loc("effek"), new EffekAssetLoader());
        }
    }
}