package mod.chloeprime.aaaparticles.mixin.client;

import mod.chloeprime.aaaparticles.client.internal.RenderContext;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow @Final public ItemInHandRenderer itemInHandRenderer;

    @Inject(method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;FLorg/joml/Matrix4fc;)V"))
    private void beforeRenderHand(DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!RenderContext.renderLevelAfterHand()) {
            boolean renderHand = true;
            EffekRenderer.renderWorldEffeks(deltaTracker, renderHand, itemInHandRenderer);
        }
    }

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void renderLevelTail(DeltaTracker deltaTracker, CallbackInfo ci) {
        if (RenderContext.renderLevelAfterHand()) {
            boolean renderHand = true;
            EffekRenderer.renderWorldEffeks(deltaTracker, renderHand, itemInHandRenderer);
        }
    }
}
