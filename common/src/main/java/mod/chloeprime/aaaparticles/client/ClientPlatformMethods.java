package mod.chloeprime.aaaparticles.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.aaaparticles.PlatformMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.function.Consumer;

public interface ClientPlatformMethods {
    static ClientPlatformMethods get() {
        if (PlatformMethods.get().isDedicatedServerDist()) {
            throw new IllegalStateException("Trying to load client-only platform methods on dedicated server");
        }
        return ClientPlatformMethodsImpl.INSTANCE;
    }

    int getDepthFormat(RenderTarget fb);
    void syncStencilState(RenderTarget from, RenderTarget to);
    void applyItemTransform(PoseStack poseStack, BakedModel model, ItemDisplayContext context, boolean applyLeftHandTransform);
    void addClientPostTickCallback(Consumer<Minecraft> action);
}
