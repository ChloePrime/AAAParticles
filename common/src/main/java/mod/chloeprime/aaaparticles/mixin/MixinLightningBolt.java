package mod.chloeprime.aaaparticles.mixin;

import mod.chloeprime.aaaparticles.common.internal.EffekLightningBolt;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LightningBolt.class)
public abstract class MixinLightningBolt implements EffekLightningBolt {
    @Unique private boolean aaaParticles$effekTicket = true;

    @Override
    public boolean aaaParticles$getEffekTicket() {
        if (aaaParticles$effekTicket) {
            aaaParticles$effekTicket = false;
            return true;
        }
        return false;
    }
}
