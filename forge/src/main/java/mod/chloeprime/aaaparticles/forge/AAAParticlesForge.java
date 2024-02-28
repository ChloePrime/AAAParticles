package mod.chloeprime.aaaparticles.forge;

import dev.architectury.platform.forge.EventBuses;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AAAParticles.MOD_ID)
public class AAAParticlesForge {
    public AAAParticlesForge() {
        var modbus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(AAAParticles.MOD_ID, modbus);
        AAAParticles.init();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            AAAParticlesClient.init();
            MinecraftForge.EVENT_BUS.addListener(AAAParticlesForgeClient::onClientSetup);
        });
    }
}