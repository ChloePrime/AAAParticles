package mod.chloeprime.aaaparticles.forge;

import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.common.network.S2CAddParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

@Mod(AAAParticles.MOD_ID)
public class AAAParticlesForge {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            AAAParticles.PARTICLE_PACKET_ID,
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public AAAParticlesForge() {
        var modbus = FMLJavaModLoadingContext.get().getModEventBus();
        //EventBuses.registerModEventBus(AAAParticles.MOD_ID, modbus);

        CHANNEL.registerMessage(0, S2CAddParticle.class,
                S2CAddParticle::encode,
                S2CAddParticle::new,
                this::messageConsumer);

        if (Environment.get().getDist().isClient()) {
            AAAParticlesClient.init();
            modbus.addListener(AAAParticlesForgeClient::onClientSetup);
        }
    }

    public void messageConsumer(S2CAddParticle particle, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                // Make sure it's only executed on the physical client
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> AAAParticlesForgeClient.handlePacket(particle, ctx))
        );
        ctx.get().setPacketHandled(true);
    }
}