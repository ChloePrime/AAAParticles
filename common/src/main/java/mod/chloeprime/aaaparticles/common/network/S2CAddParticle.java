package mod.chloeprime.aaaparticles.common.network;

import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class S2CAddParticle extends ParticleEmitterInfo {

    public S2CAddParticle(ResourceLocation effek) {
        super(effek);
    }

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
}
