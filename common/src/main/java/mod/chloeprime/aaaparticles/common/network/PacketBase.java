package mod.chloeprime.aaaparticles.common.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

interface PacketBase extends CustomPacketPayload {
    @Override
    default @NotNull Type<? extends CustomPacketPayload> type() {
        return Objects.requireNonNull(ModNetwork.TYPE_TO_ID_MAP.get(getClass()));
    }
}
