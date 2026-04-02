package mod.chloeprime.aaaparticles.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.internal.ReloadTrackable;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import mod.chloeprime.aaaparticles.client.util.*;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL41C.*;

public class RenderUtil {
    public static void copyDepthSafely(RenderTarget from, RenderTarget to) {
        var read = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var draw = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        to.copyDepthFrom(from);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, read);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, draw);
    }

    public static void copyCurrentDepthTo(RenderTarget target) {
        var frameBuffer = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        var window = MC.getWindow();
        copyDepthSafely(frameBuffer, window.getWidth(), window.getHeight(), target);
    }

    public static void copyCurrentTo(RenderTarget target) {
        var frameBuffer = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        var window = MC.getWindow();
        copyDepthSafely(frameBuffer, window.getWidth(), window.getHeight(), target);
    }

    public static void pasteToCurrentDepthFrom(RenderTarget source) {
        var frameBuffer = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        var window = MC.getWindow();
        copyDepthSafely(source, frameBuffer, window.getWidth(), window.getHeight());
    }


    public static void copyDepthSafely(int src, int srcWidth, int srcHeight, RenderTarget target) {
        var readBackup = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var drawBackup = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, src);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, target.frameBufferId);
        glBlitFramebuffer(0, 0, srcWidth, srcHeight, 0, 0, target.width, target.height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, readBackup);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawBackup);
    }

    public static void copyDepthSafely(RenderTarget src, int target, int targetWidth, int targetHeight) {
        var readBackup = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var drawBackup = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, src.frameBufferId);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, target);
        glBlitFramebuffer(0, 0, src.width, src.height, 0, 0, targetWidth, targetHeight, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, readBackup);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawBackup);
    }

    public static void copySafely(RenderTarget src, RenderTarget target) {
        copySafely(src, target, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void copySafely(RenderTarget src, RenderTarget target, int attachmentFilter) {
        var readBackup = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var drawBackup = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, src.frameBufferId);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, target.frameBufferId);
        glBlitFramebuffer(0, 0, src.width, src.height, 0, 0, target.width, target.height, attachmentFilter, GL_NEAREST);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, readBackup);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawBackup);
    }

    public static void runFrameBufferCodeSafely(Runnable code) {
        var readBackup = GL11.glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
        var drawBackup = GL11.glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        code.run();
        glBindFramebuffer(GL_READ_FRAMEBUFFER, readBackup);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawBackup);
    }

    private static final int MAX_UNIFORMS = 128;
    private static final int MAX_UNIFORMS_NAME_SIZE = 1024;
    private static final int MAX_UNIFORMS_VALUE_SIZE = 16;
    private static final ThreadLocal<IntBuffer> UNIFORMS_LENGTHS = ThreadLocal.withInitial(() -> IntBuffer.allocate(MAX_UNIFORMS));
    private static final ThreadLocal<IntBuffer> UNIFORMS_SIZES = ThreadLocal.withInitial(() -> IntBuffer.allocate(MAX_UNIFORMS));
    private static final ThreadLocal<IntBuffer> UNIFORMS_TYPES = ThreadLocal.withInitial(() -> IntBuffer.allocate(MAX_UNIFORMS));
    private static final ThreadLocal<IntBuffer> UNIFORMS_LOCATIONS = ThreadLocal.withInitial(() -> IntBuffer.allocate(MAX_UNIFORMS));
    private static final ThreadLocal<ByteBuffer[]> UNIFORMS_NAMES = ThreadLocal.withInitial(() -> new ByteBuffer[MAX_UNIFORMS]);
    private static final ThreadLocal<IntBuffer[]> UNIFORMS_VALUES_I = ThreadLocal.withInitial(() -> new IntBuffer[MAX_UNIFORMS]);
    private static final ThreadLocal<FloatBuffer[]> UNIFORMS_VALUES_F = ThreadLocal.withInitial(() -> new FloatBuffer[MAX_UNIFORMS]);
    private static final ThreadLocal<DoubleBuffer[]> UNIFORMS_VALUES_D = ThreadLocal.withInitial(() -> new DoubleBuffer[MAX_UNIFORMS]);
    private static final ThreadLocal<boolean[]> SUCCESSES = ThreadLocal.withInitial(() -> new boolean[MAX_UNIFORMS]);

    private static @NotNull ByteBuffer getUniformNameBuffer(ByteBuffer[] names, int i) {
        if (names[i] != null) {
            return names[i];
        } else {
            return names[i] = BufferUtils.createByteBuffer(MAX_UNIFORMS_NAME_SIZE);
        }
    }

    private static @NotNull IntBuffer getUniformIntValueBuffer(IntBuffer[] names, int i) {
        if (names[i] != null) {
            return names[i];
        } else {
            return names[i] = BufferUtils.createIntBuffer(MAX_UNIFORMS_VALUE_SIZE);
        }
    }

    private static @NotNull FloatBuffer getUniformFloatValueBuffer(FloatBuffer[] names, int i) {
        if (names[i] != null) {
            return names[i];
        } else {
            return names[i] = BufferUtils.createFloatBuffer(MAX_UNIFORMS_VALUE_SIZE);
        }
    }

    private static @NotNull DoubleBuffer getUniformDoubleValueBuffer(DoubleBuffer[] names, int i) {
        if (names[i] != null) {
            return names[i];
        } else {
            return names[i] = BufferUtils.createDoubleBuffer(MAX_UNIFORMS_VALUE_SIZE);
        }
    }

    private static int getUniforms(int program) {
        if (program == GL_ZERO) {
            return 0;
        }
        var successes = SUCCESSES.get();
        var uniformLengths = (IntBuffer) UNIFORMS_LENGTHS.get();
        var uniformSizes = (IntBuffer) UNIFORMS_SIZES.get();
        var uniformTypes = (IntBuffer) UNIFORMS_TYPES.get();
        var uniformLocations = (IntBuffer) UNIFORMS_LOCATIONS.get();
        var uniformValuesI = (IntBuffer[]) UNIFORMS_VALUES_I.get();
        var uniformValuesF = (FloatBuffer[]) UNIFORMS_VALUES_F.get();
        var uniformValuesD = (DoubleBuffer[]) UNIFORMS_VALUES_D.get();
        int uniformC = Math.min(MAX_UNIFORMS, glGetProgrami(program, GL_ACTIVE_UNIFORMS));
        for (int i = 0; i < uniformC; i++) {
            successes[i] = false;
            var name = getUniformNameBuffer(UNIFORMS_NAMES.get(), i);
            var uniform = ActiveUniform.fetchAndGetInstance(program, i, name);
            if (logGlError(i, program, "GL Error {} when getting uniform info from uniform {} of program {}")) {
                continue;
            }
            uniformLengths.put(i, uniform.length());
            uniformSizes.put(i, uniform.size());
            uniformTypes.put(i, uniform.type());

            int location = glGetUniformLocation(program, name);
            if (logGlError(i, program, "GL Error {} when getting uniform location from uniform {} of program {}")) {
                continue;
            }
            uniformLocations.put(i, location);

            int type = uniform.type();
            var valueI = getUniformIntValueBuffer(uniformValuesI, i);
            var valueF = getUniformFloatValueBuffer(uniformValuesF, i);
            var valueD = getUniformDoubleValueBuffer(uniformValuesD, i);
            UniformTypeRegistry.get(type, program, location, valueI, valueF, valueD);
            if (logGlError(i, program, "GL Error {} when getting uniform value from uniform {} of program {}")) {
                continue;
            }
            successes[i] = true;
        }
        return uniformC;
    }

    private static void setUniforms(int program, int count) {
        if (program == GL_ZERO) {
            return;
        }
        var successes = (boolean[]) SUCCESSES.get();
        var types = UNIFORMS_TYPES.get();
        var locations = UNIFORMS_TYPES.get();
        var uniformValuesI = (IntBuffer[]) UNIFORMS_VALUES_I.get();
        var uniformValuesF = (FloatBuffer[]) UNIFORMS_VALUES_F.get();
        var uniformValuesD = (DoubleBuffer[]) UNIFORMS_VALUES_D.get();
        for (int i = 0; i < count; i++) {
            if (!successes[i]) {
                continue;
            }
            var valueI = getUniformIntValueBuffer(uniformValuesI, i);
            var valueF = getUniformFloatValueBuffer(uniformValuesF, i);
            var valueD = getUniformDoubleValueBuffer(uniformValuesD, i);
            UniformTypeRegistry.set(types.get(i), program, locations.get(i), valueI, valueF, valueD);
            successes[i] = false;
        }
    }

    private static boolean logGlError(int i, int program, String logText) {
        int error = glGetError();
        if (error != GL_NO_ERROR) {
            AAAParticles.LOGGER.error("GL Error {} when getting info from uniform {} of program {}", error, i, program);
            return true;
        } else {
            return false;
        }
    }

    public static void runForeignRenderCodeSafely(Runnable code) {
        runPixelStoreCodeSafely(() -> {
            // tex
            int tex = glGetInteger(GL_TEXTURE_BINDING_2D);
            int magFilter = glGetTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER);
            int minFilter = glGetTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER);
            // program
            int program = glGetInteger(GL_CURRENT_PROGRAM);
            int uniformC = getUniforms(program);
            // run wrapped code
            runFrameBufferCodeSafely(code);
            // program
            glUseProgram(program);
            setUniforms(program, uniformC);
            // texture
            glBindTexture(GL_TEXTURE_2D, tex);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
            // unbind samplers
            // THIS IS THE CRITICAL FIX!!!
            glBindSampler(5, 0);
            glBindSampler(6, 0);
            glBindSampler(7, 0);
            // GlStateManager
            GlDebug.pushDebugGroup(GlDebugIds.STATE_BACK_SYNC, () -> "Sync OpenGL state to GlStateManager");
            GlStateSyncer.syncStateFromGpu(GL_ACTIVE_TEXTURE, RenderSystem::activeTexture);
            RenderSystem.assertOnRenderThread();
            GlStateSyncer.syncStateFromGpu(GL_BLEND, RenderSystem::enableBlend, RenderSystem::disableBlend);
            GlStateSyncer.syncStateFromGpu(GL_BLEND_EQUATION_RGB, RenderSystem::blendEquation);
            GlStateSyncer.syncBlendFuncFromGpu();
            GlStateSyncer.syncStateFromGpu(GL_COLOR_LOGIC_OP, RenderSystem::enableColorLogicOp, RenderSystem::disableColorLogicOp);
            GlStateSyncer.syncStateFromGpu(GL_CULL_FACE, RenderSystem::enableCull, RenderSystem::disableCull);
            GlStateSyncer.syncStateFromGpu(GL_DEPTH_TEST, RenderSystem::enableDepthTest, RenderSystem::disableDepthTest);
            GlStateSyncer.syncStateFromGpu(GL_DEPTH_FUNC, RenderSystem::depthFunc);
            GlStateSyncer.syncStateFromGpu(GL_DEPTH_WRITEMASK, RenderSystem::depthMask);
            GlStateSyncer.syncLogicOpFromGpu();
            GlDebug.popDebugGroup();
        });
    }

    /**
     * @deprecated Use {@link #runForeignRenderCodeSafely(Runnable)} instead
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static void runPixelStoreCodeSafely(Runnable code) {
        var packAlignment = glGetInteger(GL_PACK_ALIGNMENT);
        var unpackRowLength = glGetInteger(GL_UNPACK_ROW_LENGTH);
        var unpackSkipRows = glGetInteger(GL_UNPACK_SKIP_ROWS);
        var unpackSkipPixels = glGetInteger(GL_UNPACK_SKIP_PIXELS);
        var unpackAlignment = glGetInteger(GL_UNPACK_ALIGNMENT);
        code.run();
        glPixelStorei(GL_PACK_ALIGNMENT, packAlignment);
        glPixelStorei(GL_UNPACK_ROW_LENGTH, unpackRowLength);
        glPixelStorei(GL_UNPACK_SKIP_ROWS, unpackSkipRows);
        glPixelStorei(GL_UNPACK_SKIP_PIXELS, unpackSkipPixels);
        glPixelStorei(GL_UNPACK_ALIGNMENT, unpackAlignment);
    }

    public static void runForeignRenderCodeHealthily(Runnable code) {
        runForeignRenderCodeSafely(() -> {
            glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
            glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
            glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
            code.run();
        });
    }

    /**
     * @deprecated Use {@link #runForeignRenderCodeHealthily(Runnable)} instead
     */
    @Deprecated
    public static void runPixelStoreCodeHealthily(Runnable code) {
        runPixelStoreCodeSafely(() -> {
            glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
            glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
            glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
            code.run();
        });
    }

    @Deprecated
    public static <T> T supplyPixelStoreCodeHealthily(Supplier<T> code) {
        var ret = new MutableObject<T>();
        runPixelStoreCodeHealthily(() -> ret.setValue(code.get()));
        return ret.getValue();
    }

    public static <T> T supplyForeignRenderCodeHealthily(Supplier<T> code) {
        var ret = new MutableObject<T>();
        runForeignRenderCodeHealthily(() -> ret.setValue(code.get()));
        return ret.getValue();
    }

    public static boolean isReloadingResourcePacks() {
        return ((ReloadTrackable) MC).aaa_particles$isReloading();
    }

    public static void refreshBackgroundFrameBuffer() {
        refreshFrameBuffer(RenderStateCapture.DISTORTION_BACKGROUND);
    }

    public static Optional<RenderTarget> prepareBackgroundBuffer() {
        var background = getBackgroundBuffer().orElse(null);
        if (background != null) {
            var renderTarget = MC.getMainRenderTarget();
            syncStencilState(renderTarget, background);
            RenderUtil.copySafely(renderTarget, background);
            return Optional.of(background);
        } else {
            return Optional.empty();
        }
    }

    public static Optional<RenderTarget> getBackgroundBuffer() {
        if (isReloadingResourcePacks()) {
            return Optional.empty();
        }
        var background = RenderStateCapture.DISTORTION_BACKGROUND;
        return Optional.of(background);
    }

    @ExpectPlatform
    @SuppressWarnings("unused")
    public static void syncStencilState(RenderTarget from, RenderTarget to) {
        throw new AbstractMethodError();
    }

    @SuppressWarnings("SameParameterValue")
    private static void refreshFrameBuffer(RenderTarget fb) {
        RenderUtil.runForeignRenderCodeHealthily(() -> fb.resize(fb.width, fb.height, Minecraft.ON_OSX));
    }

    public static final Minecraft MC = Minecraft.getInstance();
}
