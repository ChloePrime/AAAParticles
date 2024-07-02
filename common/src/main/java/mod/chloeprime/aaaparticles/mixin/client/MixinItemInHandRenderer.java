package mod.chloeprime.aaaparticles.mixin.client;

import static mod.chloeprime.aaaparticles.client.render.RenderUtil.copyCurrentDepthTo;

import java.util.EnumMap;
import java.util.Objects;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import mod.chloeprime.aaaparticles.client.internal.EffekFpvRenderer;
import mod.chloeprime.aaaparticles.client.internal.RenderContext;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import mod.chloeprime.aaaparticles.client.render.ItemTransformHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

@Mixin(value = ItemInHandRenderer.class, priority = 1005)
public class MixinItemInHandRenderer implements EffekFpvRenderer {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private ItemRenderer itemRenderer;
    @Unique private final EnumMap<InteractionHand, RenderStateCapture> aaaParticles$captures = new EnumMap<>(InteractionHand.class);
    @Unique private final boolean aaaParticles$DISABLE_FPV_RENDERING = Boolean.getBoolean("mod.chloeprime.aaaparticles.disableFpvRendering");

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void resetCaptureState(AbstractClientPlayer player, float f, float g, InteractionHand hand, float h, ItemStack itemStack, float i, PoseStack poseStack, MultiBufferSource multiBufferSource, int j, CallbackInfo ci) {
        var capture = aaaParticles$captures.computeIfAbsent(hand, arg -> new RenderStateCapture());
        capture.hasCapture = false;
        capture.item = null;
    }

    @Inject(
            method = "renderArmWithItem",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    )
    private void setFpvRenderState(AbstractClientPlayer player, float partial, float g, InteractionHand hand, float h, ItemStack stack, float i, PoseStack poseStack, MultiBufferSource buffer, int j, CallbackInfo ci) {
        var stackTop = poseStack.last();
        var capture = Objects.requireNonNull(aaaParticles$captures.get(hand));
        capture.hasCapture = true;
        capture.pose.last().pose().load(stackTop.pose());
        capture.pose.last().normal().load(stackTop.normal());
        capture.projection.load(RenderSystem.getProjectionMatrix());
        capture.item = stack;
    }

    @Inject(method = "renderHandsWithItems", at = @At("RETURN"))
    private void captureHandDepth(float partial, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, LocalPlayer player, int i, CallbackInfo ci) {
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
        var oldProjection = RenderSystem.getProjectionMatrix();
        try {
            var camera = minecraft.gameRenderer.getMainCamera();
            aaaParticles$captures.forEach((hand, capture) -> {
                if (capture.hasCapture && capture.item != null) {
                    RenderSystem.setProjectionMatrix(capture.projection);

                    var arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
                    var tran = switch (arm) {
                        case LEFT -> ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
                        case RIGHT -> ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
                    };
                    var poseStack = capture.pose;
                    poseStack.pushPose();

                    var model = itemRenderer.getModel(capture.item, player.level, player, player.getId() + tran.ordinal());
                    ItemTransformHooks.applyItemTransform(poseStack, model, tran, arm == HumanoidArm.LEFT);
                    poseStack.translate(-0.5, -0.5, -0.5);
                    EffekRenderer.onRenderHand(partial, hand, poseStack, capture.projection, camera);

                    poseStack.popPose();
                }

                capture.item = null;
            });
        } finally {
            RenderSystem.setProjectionMatrix(oldProjection);
        }
    }
}
