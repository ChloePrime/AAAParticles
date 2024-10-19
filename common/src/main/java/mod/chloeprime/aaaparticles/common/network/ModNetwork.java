package mod.chloeprime.aaaparticles.common.network;

import dev.architectury.networking.NetworkManager;
import mod.chloeprime.aaaparticles.AAAParticles;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ModNetwork {
    static final ConcurrentHashMap<Class<?>, CustomPacketPayload.Type<?>> TYPE_TO_ID_MAP = new ConcurrentHashMap<>();
    static final AtomicInteger id = new AtomicInteger();

    public static void init() {
        register(NetworkManager.Side.S2C, S2CAddParticle.class, S2CAddParticle::encode, S2CAddParticle::new, S2CAddParticle::handle);
        register(NetworkManager.Side.S2C, S2CUpdateEmitterParam.class, S2CUpdateEmitterParam::encode, S2CUpdateEmitterParam::new, S2CUpdateEmitterParam::handle);
        register(NetworkManager.Side.S2C, S2CSendEmitterTrigger.class, S2CSendEmitterTrigger::encode, S2CSendEmitterTrigger::new, S2CSendEmitterTrigger::handle);
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends CustomPacketPayload> void register(NetworkManager.Side side, Class<T> type, StreamMemberEncoder<FriendlyByteBuf, T> encoder, StreamDecoder<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkManager.PacketContext>> handler) {
        var payloadType = new CustomPacketPayload.Type<T>(AAAParticles.loc(String.valueOf(ModNetwork.id.getAndIncrement())));
        TYPE_TO_ID_MAP.put(type, payloadType);
        var codec = StreamCodec.ofMember(encoder, decoder);
        NetworkManager.registerReceiver(side, payloadType, codec, (packet, context) -> handler.accept(packet, () -> context));
    }
}
