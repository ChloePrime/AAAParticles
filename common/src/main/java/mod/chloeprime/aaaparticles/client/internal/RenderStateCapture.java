package mod.chloeprime.aaaparticles.client.internal;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.render.RenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.*;

public class RenderStateCapture {
    public static final RenderStateCapture LEVEL = new RenderStateCapture();

    private static final Set<RenderTarget> TRACKED_FBS = new LinkedHashSet<>(3);

    public static final RenderTarget CAPTURED_WORLD_DEPTH_BUFFER = create();
    public static final RenderTarget CAPTURED_HAND_DEPTH_BUFFER = create();
    public static final RenderTarget DISTORTION_BACKGROUND = create();

    public static RenderTarget create() {
        var main = RenderUtil.MC.getMainRenderTarget();
        var result = new TextureTarget(main.width, main.height, true, Minecraft.ON_OSX);
        TRACKED_FBS.add(result);
        return result;
    }

    public static void init() {
        AAAParticles.LOGGER.info("Initialized framebuffers of {}", RenderStateCapture.class.getSimpleName());
    }

    static {
        AAAParticles.LOGGER.info("Initializing framebuffers of {}", RenderStateCapture.class.getSimpleName());
    }

    public static void onResize(int width, int height, boolean onOsx) {
        for (var fb : TRACKED_FBS) {
            fb.resize(width, height, onOsx);
        }
    }

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
