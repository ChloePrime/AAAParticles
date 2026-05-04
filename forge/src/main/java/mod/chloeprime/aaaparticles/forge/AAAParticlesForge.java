package mod.chloeprime.aaaparticles.forge;

import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

@Mod(AAAParticles.MOD_ID)
public class AAAParticlesForge extends AAAParticles  {
    public AAAParticlesForge(ModContainer container) {
        AAAParticles.init();

        if (FMLLoader.getCurrent().getDist().isClient()) {
            AAAParticlesForgeClient.onClientInit(container);
            AAAParticlesClient.init();
        }
    }
}