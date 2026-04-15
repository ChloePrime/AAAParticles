package mod.chloeprime.aaaparticles.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.aaaparticles.client.ClientPlatformMethods;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

public class ItemTransformHooks {
    public static void applyItemTransform(PoseStack poseStack, BakedModel model, ItemDisplayContext context, boolean applyLeftHandTransform) {
        ClientPlatformMethods.get().applyItemTransform(poseStack, model, context, applyLeftHandTransform);
    }
}
