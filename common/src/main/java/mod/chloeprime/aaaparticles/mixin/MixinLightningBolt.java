package mod.chloeprime.aaaparticles.mixin;

import mod.chloeprime.aaaparticles.client.ModClientHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public abstract class MixinLightningBolt extends Entity {
    @Shadow private int life;

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/Entity;tick()V")
    )
    private void playLightingEffek(CallbackInfo ci) {
        if (life == 0 && level.isClientSide()) {
            ModClientHooks.playLightningEffek((LightningBolt) (Object) this);
        }
    }

    public MixinLightningBolt(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
}
