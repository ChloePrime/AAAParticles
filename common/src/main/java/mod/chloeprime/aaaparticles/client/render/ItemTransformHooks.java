package mod.chloeprime.aaaparticles.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;

public class ItemTransformHooks {
    @ExpectPlatform
    public static void applyItemTransform(PoseStack poseStack, BakedModel model, ItemTransforms.TransformType context, boolean applyLeftHandTransform) {
        throw new AssertionError();
    }
}
