package mod.chloeprime.aaaparticles.fabric.client;

import com.google.auto.service.AutoService;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.aaaparticles.client.ClientPlatformMethods;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.function.Consumer;

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

    @Override
    public void addClientPostTickCallback(Consumer<Minecraft> action) {
        ClientTickEvents.END_CLIENT_TICK.register(action::accept);
    }
}
