package mod.chloeprime.aaaparticles.forge;

import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import mod.chloeprime.aaaparticles.client.loader.EffekAssetLoader;
import mod.chloeprime.aaaparticles.client.render.RenderUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

@EventBusSubscriber(Dist.CLIENT)
public class AAAParticlesForgeClient extends AAAParticles {
    public static void onClientInit() {
    }

    @SuppressWarnings("unused")
    static void onClientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(AAAParticlesClient::setup);
        e.enqueueWork(() -> RenderUtil.runPixelStoreCodeHealthily(RenderStateCapture::init));
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddClientReloadListenersEvent event) {
        if (!NativePlatform.isDataGen()) {
            event.addListener(AAAParticles.loc("effek"), new EffekAssetLoader());
        }
    }
}
