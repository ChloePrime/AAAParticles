package mod.chloeprime.aaaparticles.fabric;

import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class AAAParticlesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AAAParticlesClient.init();
        AAAParticlesClient.setup();
        EffekRenderer.init();
        WorldRenderEvents.LAST.register(
                ctx -> EffekRenderer.onRenderWorldLast(ctx.tickDelta(), ctx.matrixStack(), ctx.projectionMatrix())
        );
    }
}