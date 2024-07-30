package mod.chloeprime.aaaparticles.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;

import mod.chloeprime.aaaparticles.client.ModClientConfig;
import mod.chloeprime.aaaparticles.client.ModClientHooks;
import mod.chloeprime.aaaparticles.common.internal.EffekLightningBolt;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBoltRenderer.class)
public class MixinLightningBoltRenderer {
    @Inject(
            method = "render(Lnet/minecraft/world/entity/LightningBolt;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), cancellable = true
    )
    private void disableVanillaLightningModelAndPlayEffek(LightningBolt bolt, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (!ModClientConfig.isLightningEnabled()) {
            return;
        }
        if (((EffekLightningBolt) bolt).aaaParticles$getEffekTicket()) {
            ModClientHooks.playLightningEffek(bolt);
        }
        ci.cancel();
    }
}
