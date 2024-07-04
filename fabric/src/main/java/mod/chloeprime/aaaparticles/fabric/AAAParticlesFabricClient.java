package mod.chloeprime.aaaparticles.fabric;

import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import mod.chloeprime.aaaparticles.common.network.S2CAddParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class AAAParticlesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AAAParticlesClient.init();
        AAAParticlesClient.setup();
        EffekRenderer.init();

        ClientPlayNetworking.registerGlobalReceiver(AAAParticles.PARTICLE_PACKET_ID, (client, handler, buf, responseSender) -> {
            var particle = new S2CAddParticle(buf);
            client.execute(() -> particle.spawnInWorld(client.level, client.player));
        });
    }
}