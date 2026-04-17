package mod.chloeprime.aaaparticles.common.network;

import dev.architectury.networking.NetworkManager;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Experimental
public class S2CSendEmitterTrigger implements PacketBase {
    private final ParticleEmitter.Type type;
    private final Identifier effek;
    private final Identifier emitterName;
    private final int[] triggers;

    public S2CSendEmitterTrigger(ParticleEmitter.Type type, Identifier effek, Identifier emitterName, int[] triggers) {
        this.type = type;
        this.effek = effek;
        this.emitterName = emitterName;
        this.triggers = triggers;
    }

    S2CSendEmitterTrigger(FriendlyByteBuf buf) {
        type = buf.readEnum(ParticleEmitter.Type.class);
        effek = buf.readIdentifier();
        emitterName = buf.readIdentifier();
        this.triggers = buf.readVarIntArray();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(type);
        buf.writeIdentifier(effek);
        buf.writeIdentifier(emitterName);
        buf.writeVarIntArray(triggers);
    }

    public void handle(Supplier<NetworkManager.PacketContext> ctx) {
        var context = ctx.get();
        context.queue(() -> AAAParticlesClient.sendTrigger(type, effek, emitterName, triggers));
    }
}
