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

    // I would suggest a more robust solution with additional methods for registering on both sides, however for our use cases this will suffice.
    // I found this solution on https://github.com/Buuz135/FindMe/commit/b73519f2d9918e3dfb9b2655178230270dcdd7e0 's repository, many thanks!
    @SuppressWarnings("SameParameterValue")
    private static <T extends CustomPacketPayload> void register(NetworkManager.Side side, Class<T> type, StreamMemberEncoder<FriendlyByteBuf, T> encoder, StreamDecoder<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkManager.PacketContext>> handler) {
        CustomPayload.Id<T> payloadType = new CustomPayload.Id(AAAParticles.loc(String.valueOf(id.getAndIncrement())));
        TYPE_TO_ID_MAP.put(type, payloadType);
        PacketCodec<PacketByteBuf, T> codec = PacketCodec.of(encoder, decoder);

        if (Platform.getEnvironment().equals(Env.SERVER)) {
            NetworkAggregator.registerS2CType(payloadType, codec, List.of());
        } else {
            NetworkAggregator.registerReceiver(NetworkManager.s2c(), payloadType, codec, Collections.emptyList(), (packet, context) -> handler.accept(packet, (Supplier)() -> context));
        }
    }
}
