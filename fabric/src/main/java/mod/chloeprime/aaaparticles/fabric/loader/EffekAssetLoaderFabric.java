package mod.chloeprime.aaaparticles.fabric.loader;

import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.loader.EffekAssetLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

public class EffekAssetLoaderFabric extends EffekAssetLoader implements IdentifiableResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return AAAParticles.loc("effek");
    }
}
