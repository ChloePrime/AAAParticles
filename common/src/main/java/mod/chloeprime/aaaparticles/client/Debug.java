package mod.chloeprime.aaaparticles.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.platform.Platform;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerEffect;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import mod.chloeprime.aaaparticles.client.registry.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Optional;

/**
 * @author ChloePrime
 */
public enum Debug {
    INSTANCE;
    public static final boolean DEBUG_ENABLED = Platform.isDevelopmentEnvironment() || Boolean.getBoolean("mod.chloeprime.aaaparticles.debug");
    private static final int DEBUG_KEY = InputConstants.KEY_F;
    private static final ResourceLocation DEBUG_PARTICLE = new ResourceLocation(AAAParticles.MOD_ID, "debug");

    public void registerDebugHooks() {
        if (!DEBUG_ENABLED) {
            return;
        }
        ClientRawInputEvent.KEY_PRESSED.register(this::keyPressed);
        InteractionEvent.CLIENT_LEFT_CLICK_AIR.register(this::leftClick);
    }

    public EventResult keyPressed(Minecraft client, int keyCode, int scanCode, int action, int modifiers) {
        keyPressed0(client, keyCode, scanCode, action, modifiers);
        return EventResult.pass();
    }

    public static void keyPressed0(Minecraft client, int keyCode, int scanCode, int action, int modifiers) {
        if (action != InputConstants.PRESS || NativePlatform.isRunningOnUnsupportedPlatform()) {
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

        Optional.ofNullable(mc.crosshairPickEntity).map(Entity::getId)
                .ifPresent(eid -> {
                    var dim = Objects.requireNonNull(mc.level).dimension();
                    var srv = Objects.requireNonNull(mc.getSingleplayerServer());
                    srv.execute(() -> {
                        var srvLevel = Objects.requireNonNull(srv.getLevel(dim));
                        AAALevel.addParticle(srvLevel, new ParticleEmitterInfo(DEBUG_PARTICLE)
                                        .bindOnEntity(Objects.requireNonNull(srvLevel.getEntity(eid)))
//                        .position(0, 1.8, 0)
//                        .entitySpaceRelativePosition(0.5, 0, -0.5)
//                        .rotation((float) (Math.PI / 2), 0, 0)
                        );
                    });
                });
    }

    public void leftClick(Player player, InteractionHand hand) {
        if (NativePlatform.isRunningOnUnsupportedPlatform()) {
            return;
        }
        var type = Math.random() <= 0.5 ? ParticleEmitter.Type.FIRST_PERSON_MAINHAND : ParticleEmitter.Type.FIRST_PERSON_OFFHAND;
        var emitter = Objects.requireNonNull(EffectRegistry.get(DEBUG_PARTICLE)).play(type);
        emitter.setPosition(0, 0.5F, 0);
        emitter.setScale(0.1F, 0.1F, 0.1F);
    }
}
