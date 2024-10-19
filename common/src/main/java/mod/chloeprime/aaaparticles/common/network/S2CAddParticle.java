package mod.chloeprime.aaaparticles.common.network;

import dev.architectury.networking.NetworkManager.PacketContext;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public class S2CAddParticle extends ParticleEmitterInfo implements PacketBase {
    /**
     * @see #create(Level, ResourceLocation)
     */
    @ApiStatus.Internal
    public S2CAddParticle(ResourceLocation effek) {
        super(effek);
    }

    /**
     * @see #create(Level, ResourceLocation, ResourceLocation)
     */
    @ApiStatus.Internal
    public S2CAddParticle(ResourceLocation effek, ResourceLocation emitter) {
        super(effek, emitter);
    }

    public S2CAddParticle(ParticleEmitterInfo toCopy) {
        super(toCopy.effek, toCopy.emitter);
        toCopy.copyTo(this);
    }

    public static S2CAddParticle of(ParticleEmitterInfo info) {
        return info instanceof S2CAddParticle packet ? packet : new S2CAddParticle(info);
    }

    public S2CAddParticle(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public S2CAddParticle clone() {
        return (S2CAddParticle) super.clone();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        super.encode(buf);
    }

    public void handle(Supplier<PacketContext> ctx) {
        var context = ctx.get();
        var player = context.getPlayer();
        context.queue(() -> spawnInWorld(player.level(), player));
    }
}
