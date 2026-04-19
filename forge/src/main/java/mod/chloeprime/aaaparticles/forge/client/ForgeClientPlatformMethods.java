package mod.chloeprime.aaaparticles.forge.client;

import com.google.auto.service.AutoService;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.aaaparticles.client.ClientPlatformMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;

@AutoService(ClientPlatformMethods.class)
public class ForgeClientPlatformMethods implements ClientPlatformMethods {
    @Override
    public int getDepthFormat(RenderTarget fb) {
        return fb.isStencilEnabled() ? 2 : 1;
    }

    @Override
    public void syncStencilState(RenderTarget from, RenderTarget to) {
        if (from.isStencilEnabled() && !to.isStencilEnabled()) {
            to.enableStencil();
        }
    }

    @Override
    public void applyItemTransform(PoseStack poseStack, BakedModel model, ItemDisplayContext context, boolean applyLeftHandTransform) {
        model.applyTransform(context, poseStack, applyLeftHandTransform);
    }

    @Override
    public void addClientPostTickCallback(Consumer<Minecraft> action) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.class, event -> {
            if (event.phase == TickEvent.Phase.END) {
                action.accept(Minecraft.getInstance());
            }
        });
    }
}
