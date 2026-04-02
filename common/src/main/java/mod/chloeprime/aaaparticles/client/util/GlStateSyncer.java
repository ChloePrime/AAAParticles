package mod.chloeprime.aaaparticles.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.function.IntConsumer;
import java.util.function.ToIntFunction;

import static org.lwjgl.opengl.GL41C.*;

/**
 * Sync GL state from GPU Driver to {@link GlStateManager}
 */
public class GlStateSyncer {
    public static void syncStateFromGpu(int glEnum, Runnable ifTrue, Runnable ifFalse) {
        RenderSystem.assertOnRenderThread();
        (glIsEnabled(glEnum) ? ifTrue : ifFalse).run();
    }

    public static void syncStateFromGpu(int glEnum, BooleanConsumer behavior) {
        RenderSystem.assertOnRenderThread();
        behavior.accept(glGetBoolean(glEnum));
    }

    public static void syncStateFromGpu(int glEnum, IntConsumer behavior) {
        RenderSystem.assertOnRenderThread();
        behavior.accept(glGetInteger(glEnum));
    }

    public static void syncLogicOpFromGpu() {
        RenderSystem.assertOnRenderThread();
        var op = glGetInteger(GL_LOGIC_OP_MODE);
        RenderSystem.logicOp(LOGIC_OPS.getOrDefault(op, GlStateManager.LogicOp.SET));
    }

    public static void syncBlendFuncFromGpu() {
        RenderSystem.assertOnRenderThread();
        var srcRGB = glGetInteger(GL_BLEND_SRC_RGB);
        var dstRGB = glGetInteger(GL_BLEND_DST_RGB);
        var srcAlpha = glGetInteger(GL_BLEND_SRC_ALPHA);
        var dstAlpha = glGetInteger(GL_BLEND_DST_ALPHA);
        RenderSystem.blendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
    }

    private static final Int2ObjectMap<GlStateManager.LogicOp> LOGIC_OPS = createIdToEnumTable(GlStateManager.LogicOp.values(), lop -> lop.value);

    private static <E> Int2ObjectMap<E> createIdToEnumTable(E[] entries, ToIntFunction<E> getter) {
        var map = new Int2ObjectOpenHashMap<E>();
        for (E entry : entries) {
            map.put(getter.applyAsInt(entry), entry);
        }
        return Int2ObjectMaps.unmodifiable(map);
    }
}
