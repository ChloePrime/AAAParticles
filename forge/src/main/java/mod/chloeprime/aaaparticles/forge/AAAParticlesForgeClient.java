package mod.chloeprime.aaaparticles.forge;

import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.common.network.S2CAddParticle;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AAAParticlesForgeClient {
    @SuppressWarnings("unused")
    static void onClientSetup(FMLClientSetupEvent e) {
        AAAParticlesClient.setup();
    }

    public static void handlePacket(S2CAddParticle particle, Supplier<NetworkEvent.Context> ctx)
    {
        particle.spawnInWorld(Minecraft.getInstance().level, Minecraft.getInstance().player);
    }
}
