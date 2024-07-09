package mod.chloeprime.aaaparticles.fabric;

import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import mod.chloeprime.aaaparticles.fabric.loader.EffekAssetLoaderFabric;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class AAAParticlesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AAAParticlesClient.init();
        AAAParticlesClient.setup();
        EffekRenderer.init();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new EffekAssetLoaderFabric());
    }
}