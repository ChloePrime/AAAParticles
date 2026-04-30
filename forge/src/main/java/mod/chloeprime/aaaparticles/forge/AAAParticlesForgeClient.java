package mod.chloeprime.aaaparticles.forge;

import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAClientConfig;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import mod.chloeprime.aaaparticles.client.render.RenderUtil;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class AAAParticlesForgeClient extends AAAParticles {
    public static void onClientInit() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AAAClientConfig.SPEC);
    }

    @SuppressWarnings("unused")
    static void onClientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(AAAParticlesClient::setup);
        e.enqueueWork(() -> RenderUtil.runPixelStoreCodeHealthily(RenderStateCapture::init));
    }
}
