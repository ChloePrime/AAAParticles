package mod.chloeprime.aaaparticles.api.common;

import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.common.network.ModNetwork;
import mod.chloeprime.aaaparticles.common.network.S2CAddParticle;
import mod.chloeprime.aaaparticles.common.network.S2CSendEmitterTrigger;
import mod.chloeprime.aaaparticles.common.network.S2CUpdateEmitterParam;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

public class AAALevel {
    public static void addParticle(Level level, ParticleEmitterInfo info) {
        addParticle(level, false, info);
    }

    public static void addParticle(Level level, boolean force, ParticleEmitterInfo info) {
        addParticle(level, force ? 512 : 32, info);
    }

    public static void addParticle(Level level, double distance, ParticleEmitterInfo info) {
        if (level.isClientSide()) {
            AAAParticlesClient.addParticle(level, info);
        } else {
            var packet = S2CAddParticle.of(info);
            var serverLevel = ((ServerLevel) level);
            var sqrDistance = distance * distance;
            for (var player : serverLevel.players()) {
                sendToPlayer(player, serverLevel, packet, sqrDistance);
            }
        }
    }

    @ApiStatus.Experimental
    public static void setParameterFor(Player player, ParticleEmitter.Type type, ResourceLocation effek, ResourceLocation emitterName, DynamicParameter[] parameters) {
        if (player.level().isClientSide) {
            AAAParticlesClient.setParam(type, effek, emitterName, parameters);
        } else {
            ModNetwork.CHANNEL.sendToPlayer((ServerPlayer) player, new S2CUpdateEmitterParam(type, effek, emitterName, parameters));
        }
    }

    @ApiStatus.Experimental
    public static void sendTriggerFor(Player player, ParticleEmitter.Type type, ResourceLocation effek, ResourceLocation emitterName, int[] triggers) {
        if (player.level().isClientSide) {
            AAAParticlesClient.sendTrigger(type, effek, emitterName, triggers);
        } else {
            ModNetwork.CHANNEL.sendToPlayer((ServerPlayer) player, new S2CSendEmitterTrigger(type, effek, emitterName, triggers));
        }
    }

    private static void sendToPlayer(ServerPlayer player, Level level, S2CAddParticle packet, double sqrDistance) {
        if (player.level() != level) {
            return;
        }
        if (packet.isPositionSet()) {
            if (player.position().distanceToSqr(packet.position()) > sqrDistance) {
                return;
            }
        }
        ModNetwork.CHANNEL.sendToPlayer(player, packet);
    }
}
