package mod.chloeprime.aaaparticles.client.render.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

@SuppressWarnings("unused")
public class ItemTransformHooksImpl {
    public static void applyItemTransform(PoseStack poseStack, BakedModel model, ItemDisplayContext context, boolean applyLeftHandTransform) {
        model.applyTransform(context, poseStack, applyLeftHandTransform);
    }
}
