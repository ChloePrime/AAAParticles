package mod.chloeprime.aaaparticles.forge;

import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import mod.chloeprime.aaaparticles.client.render.RenderUtil;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class AAAParticlesForgeClient extends AAAParticles {
    public static void onClientInit() {
    }

    @SuppressWarnings("unused")
    static void onClientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(AAAParticlesClient::setup);
        e.enqueueWork(() -> RenderUtil.runPixelStoreCodeHealthily(RenderStateCapture::init));
    }
}
