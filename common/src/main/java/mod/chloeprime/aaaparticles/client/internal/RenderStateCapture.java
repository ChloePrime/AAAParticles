package mod.chloeprime.aaaparticles.client.internal;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class RenderStateCapture {
    public static final RenderStateCapture LEVEL = new RenderStateCapture();
    public static final RenderTarget CAPTURED_WORLD_DEPTH_BUFFER = new TextureTarget(
            Minecraft.getInstance().getWindow().getWidth(),
            Minecraft.getInstance().getWindow().getHeight(),
            true, Minecraft.ON_OSX
    );

    public static final RenderTarget CAPTURED_HAND_DEPTH_BUFFER = new TextureTarget(
            Minecraft.getInstance().getWindow().getWidth(),
            Minecraft.getInstance().getWindow().getHeight(),
            true, Minecraft.ON_OSX
    );

    public boolean hasCapture = false;
    public final PoseStack pose = new PoseStack();
    public final Matrix4f projection = new Matrix4f();

    /**
     * Hand Only
     */
    public ItemStack item;

    /**
     * Level Only
     */
    public Camera camera;
}
