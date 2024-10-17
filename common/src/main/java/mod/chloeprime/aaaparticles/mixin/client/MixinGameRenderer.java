package mod.chloeprime.aaaparticles.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.aaaparticles.client.internal.RenderContext;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow @Final public ItemInHandRenderer itemInHandRenderer;
    @Shadow private boolean renderHand;

    @Inject(method = "renderLevel",
            at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand:Z"))
    private void beforeRenderHand(float partial, long l, PoseStack poseStack, CallbackInfo ci) {
        if (!RenderContext.renderLevelAfterHand()) {
            EffekRenderer.renderWorldEffeks(partial, renderHand, itemInHandRenderer);
        }
    }

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void renderLevelTail(float partial, long l, PoseStack poseStack, CallbackInfo ci) {
        if (RenderContext.renderLevelAfterHand()) {
            EffekRenderer.renderWorldEffeks(partial, renderHand, itemInHandRenderer);
        }
    }
}
