package mod.chloeprime.aaaparticles.common.network;

import dev.architectury.networking.NetworkManager;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Experimental
public class S2CSendEmitterTrigger {
    private final ParticleEmitter.Type type;
    private final ResourceLocation effek;
    private final ResourceLocation emitterName;
    private final int[] triggers;

    public S2CSendEmitterTrigger(ParticleEmitter.Type type, ResourceLocation effek, ResourceLocation emitterName, int[] triggers) {
        this.type = type;
        this.effek = effek;
        this.emitterName = emitterName;
        this.triggers = triggers;
    }

    S2CSendEmitterTrigger(FriendlyByteBuf buf) {
        type = buf.readEnum(ParticleEmitter.Type.class);
        effek = buf.readResourceLocation();
        emitterName = buf.readResourceLocation();
        this.triggers = buf.readVarIntArray();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(type);
        buf.writeResourceLocation(effek);
        buf.writeResourceLocation(emitterName);
        buf.writeVarIntArray(triggers);
    }

    public void handle(Supplier<NetworkManager.PacketContext> ctx) {
        var context = ctx.get();
        context.queue(() -> AAAParticlesClient.sendTrigger(type, effek, emitterName, triggers));
    }
}
