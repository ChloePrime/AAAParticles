package mod.chloeprime.aaaparticles.client.render.fabric;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;

public class ItemTransformHooksImpl {
    public static void applyItemTransform(PoseStack poseStack, BakedModel model, ItemTransforms.TransformType context, boolean applyLeftHandTransform) {
        model.getTransforms().getTransform(context).apply(applyLeftHandTransform, poseStack);
    }
}
