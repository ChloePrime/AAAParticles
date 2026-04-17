package mod.chloeprime.aaaparticles.client.internal;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.ClientPlatformMethods;
import mod.chloeprime.aaaparticles.client.internal.mc26_1.Framebuffer;
import mod.chloeprime.aaaparticles.client.render.RenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.*;

public class RenderStateCapture {
    public static final RenderStateCapture LEVEL = new RenderStateCapture();

    private static final Set<Framebuffer> TRACKED_FBS = new LinkedHashSet<>(3);

    public static final Framebuffer CAPTURED_WORLD_DEPTH_BUFFER = create("[%s] World Depth Capture FBO".formatted(AAAParticles.MOD_NAME));
    public static final Framebuffer CAPTURED_HAND_DEPTH_BUFFER = create("[%s] Hand Depth Capture FBO".formatted(AAAParticles.MOD_NAME));
    public static final Framebuffer DISTORTION_BACKGROUND = create("[%s] Distortion Background FBO".formatted(AAAParticles.MOD_NAME));

    public static Framebuffer create(String label) {
        var main = RenderUtil.MC.getMainRenderTarget();
        var result = new Framebuffer(label, main.width, main.height, main.useDepth, ClientPlatformMethods.get().isStencilEnabled26_1(main));
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

    public CameraRenderState cameraState_26_1;
    public GpuBufferSlice projectionBuffer_26_1;
}
