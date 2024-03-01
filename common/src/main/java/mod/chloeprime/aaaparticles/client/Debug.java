package mod.chloeprime.aaaparticles.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerEffect;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;

import java.lang.ref.WeakReference;

/**
 * @author ChloePrime
 */
public enum Debug implements ClientRawInputEvent.KeyPressed {
    INSTANCE;
    private static final int DEBUG_KEY = InputConstants.KEY_F;
    private static final ResourceLocation DEBUG_PARTICLE = new ResourceLocation(AAAParticles.MOD_ID, "fire");

    @Override
    public EventResult keyPressed(Minecraft client, int keyCode, int scanCode, int action, int modifiers) { {
        keyPressed0(client, keyCode, scanCode, action, modifiers);
        return EventResult.pass();
    }}

    public static void keyPressed0(Minecraft client, int keyCode, int scanCode, int action, int modifiers) {
        if (action != InputConstants.PRESS) {
            return;
        }
        var mc = Minecraft.getInstance();
        if (keyCode == InputConstants.KEY_R) {
            LogManager.getLogger().info("ResDomains = " + mc.getResourceManager().getNamespaces());
            return;
        }
        if (keyCode == InputConstants.KEY_M) {
            var effect = new WeakReference<>(new EffekseerEffect());
            while (effect.get() != null) {
                System.gc();
            }
            return;
        }
        if (keyCode != DEBUG_KEY) {
            return;
        }
        var player = mc.player;
        if (player == null) {
            return;
        }

        AAALevel.addParticle(mc.level, new ParticleEmitterInfo(DEBUG_PARTICLE)
                .bindOnEntity(mc.player)
                .useEntityHeadSpace()
                .position(0, 4, 0)
                .entitySpaceRelativePosition(0.5, 0, -0.5)
                .rotation((float) (Math.PI / 2), 0, 0));
    }
}
