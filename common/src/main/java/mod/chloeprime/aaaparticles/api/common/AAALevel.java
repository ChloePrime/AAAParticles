package mod.chloeprime.aaaparticles.api.common;

import com.zigythebird.multiloaderutils.utils.NetworkManager;
import io.netty.buffer.Unpooled;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import mod.chloeprime.aaaparticles.common.network.S2CAddParticle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import static mod.chloeprime.aaaparticles.client.AAAParticlesClient.PACKET_ID;

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

    private static void sendToPlayer(ServerPlayer player, Level level, S2CAddParticle packet, double sqrDistance) {
        if (player.level() != level) {
            return;
        }
        if (packet.isPositionSet()) {
            if (player.position().distanceToSqr(packet.position()) > sqrDistance) {
                return;
            }
        }
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.encode(buf);
        NetworkManager.sendToPlayer(player, PACKET_ID, buf);
    }
}
