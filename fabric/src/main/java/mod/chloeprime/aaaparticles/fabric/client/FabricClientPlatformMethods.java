package mod.chloeprime.aaaparticles.fabric.client;

import com.google.auto.service.AutoService;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.aaaparticles.client.ClientPlatformMethods;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

@AutoService(ClientPlatformMethods.class)
public class FabricClientPlatformMethods implements ClientPlatformMethods {
    @Override
    public int getDepthFormat(RenderTarget fb) {
        return 1;
    }

    @Override
    public void syncStencilState(RenderTarget from, RenderTarget to) {
    }

    @Override
    public void applyItemTransform(PoseStack poseStack, BakedModel model, ItemDisplayContext context, boolean applyLeftHandTransform) {
        model.getTransforms().getTransform(context).apply(applyLeftHandTransform, poseStack);
    }
}
