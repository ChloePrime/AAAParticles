package mod.chloeprime.aaaparticles.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.aaaparticles.client.internal.EffekFpvRenderer;
import mod.chloeprime.aaaparticles.client.internal.RenderContext;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;
import java.util.Objects;

import static mod.chloeprime.aaaparticles.client.render.RenderUtil.MC;
import static mod.chloeprime.aaaparticles.client.render.RenderUtil.copyCurrentDepthTo;

@Mixin(value = ItemInHandRenderer.class, priority = 1005)
public class MixinItemInHandRenderer implements EffekFpvRenderer {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private ItemModelResolver itemModelResolver;
    @Unique private final EnumMap<InteractionHand, RenderStateCapture> aaaParticles$captures = new EnumMap<>(InteractionHand.class);
    @Unique private final boolean aaaParticles$DISABLE_FPV_RENDERING = Boolean.getBoolean("mod.chloeprime.aaaparticles.disableFpvRendering");

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void resetCaptureState(AbstractClientPlayer player, float partial, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        var capture = aaaParticles$captures.computeIfAbsent(hand, arg -> new RenderStateCapture());
        capture.hasCapture = false;
        capture.item = null;
    }

    @Inject(
            method = "renderArmWithItem",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V")
    )
    private void setFpvRenderState(AbstractClientPlayer player, float partial, float xRot, InteractionHand hand, float attack, ItemStack stack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        var stackTop = poseStack.last();
        var capture = Objects.requireNonNull(aaaParticles$captures.get(hand));
        capture.hasCapture = true;
        capture.pose.last().pose().set(stackTop.pose());
        capture.pose.last().normal().set(stackTop.normal());
        capture.item = stack;
        capture.projectionBuffer_26_1 = RenderSystem.getProjectionMatrixBuffer();
    }

    @Inject(method = "renderHandsWithItems", at = @At("RETURN"))
    private void captureHandDepth(float partial, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, LocalPlayer player, int lightCoords, CallbackInfo ci) {
        if (RenderContext.renderHandDeferred()) {
            if (RenderContext.captureHandDepth()) {
                copyCurrentDepthTo(RenderStateCapture.CAPTURED_HAND_DEPTH_BUFFER);
            }
        } else {
            aaaParticles$renderFpvEffek(partial, player);
        }
    }

    @Override
    public void aaaParticles$renderFpvEffek(float partial, LocalPlayer player) {
        if (aaaParticles$DISABLE_FPV_RENDERING) {
            return;
        }
        var oldProjection = RenderSystem.getProjectionMatrixBuffer();
        var oldVertexSort = RenderSystem.getProjectionType();
        try {
            var camera = minecraft.gameRenderer.getMainCamera();
            aaaParticles$captures.forEach((hand, capture) -> {
                if (capture.hasCapture && capture.item != null) {
                    RenderSystem.setProjectionMatrix(capture.projectionBuffer_26_1, oldVertexSort);

                    var arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
                    var tran = switch (arm) {
                        case LEFT -> ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
                        case RIGHT -> ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
                    };
                    var poseStack = capture.pose;
                    poseStack.pushPose();

                    var itemState = new ItemStackRenderState();
                    this.itemModelResolver.updateForTopItem(itemState, capture.item, tran, minecraft.level, player, player.getId() + tran.ordinal());

                    var cameraState = MC.gameRenderer.getGameRenderState().levelRenderState.cameraRenderState;
                    EffekRenderer.onRenderHand(partial, hand, poseStack, cameraState.projectionMatrix, camera, cameraState);

                    poseStack.popPose();
                }

                capture.item = null;
            });
        } finally {
            RenderSystem.setProjectionMatrix(oldProjection, oldVertexSort);
        }
    }
}
