package mod.chloeprime.aaaparticles.common.network;

import dev.architectury.networking.NetworkManager;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.DynamicParameter;
import mod.chloeprime.aaaparticles.client.AAAParticlesClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Experimental
public class S2CUpdateEmitterParam implements PacketBase {
    private final ParticleEmitter.Type type;
    private final ResourceLocation effek;
    private final ResourceLocation emitterName;
    private final DynamicParameter[] parameters;

    public S2CUpdateEmitterParam(ParticleEmitter.Type type, ResourceLocation effek, ResourceLocation emitterName, DynamicParameter[] parameters) {
        this.type = type;
        this.effek = effek;
        this.emitterName = emitterName;
        this.parameters = parameters;
    }

    S2CUpdateEmitterParam(FriendlyByteBuf buf) {
        type = buf.readEnum(ParticleEmitter.Type.class);
        effek = buf.readResourceLocation();
        emitterName = buf.readResourceLocation();

        this.parameters = new DynamicParameter[buf.readVarInt()];
        for (int i = 0; i < this.parameters.length; i++) {
            var index = buf.readVarInt();
            var value = buf.readFloat();
            parameters[i] = new DynamicParameter(index, value);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(type);
        buf.writeResourceLocation(effek);
        buf.writeResourceLocation(emitterName);

        buf.writeVarInt(parameters.length);
        for (var parameter : parameters) {
            buf.writeVarInt(parameter.index());
            buf.writeFloat(parameter.value());
        }
    }

    public void handle(Supplier<NetworkManager.PacketContext> ctx) {
        var context = ctx.get();
        context.queue(() -> AAAParticlesClient.setParam(type, effek, emitterName, parameters));
    }
}
