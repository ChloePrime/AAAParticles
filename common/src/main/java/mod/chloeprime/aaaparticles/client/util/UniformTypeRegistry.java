package mod.chloeprime.aaaparticles.client.util;

import it.unimi.dsi.fastutil.ints.IntList;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL41C.*;

public class UniformTypeRegistry {
    public static final int NUMBER_FORMAT_INT = 1;
    public static final int NUMBER_FORMAT_UINT = 2;
    public static final int NUMBER_FORMAT_FLOAT = 4;
    public static final int NUMBER_FORMAT_DOUBLE = 8;
    public static final int NUMBER_FORMAT_BOOL = 16;
    public static final int SIZE_1 = 1;
    public static final int SIZE_2 = 2;
    public static final int SIZE_3 = 3;
    public static final int SIZE_4 = 4;
    public static final int SIZE_MAT2 = 12;
    public static final int SIZE_MAT3 = 13;
    public static final int SIZE_MAT4 = 14;
    public static final int SIZE_MAT2x3 = 23;
    public static final int SIZE_MAT2x4 = 24;
    public static final int SIZE_MAT3x2 = 32;
    public static final int SIZE_MAT3x4 = 34;
    public static final int SIZE_MAT4x2 = 42;
    public static final int SIZE_MAT4x3 = 43;

    public static int numberFormat(int type) {
        return switch (type) {
            case GL_FLOAT,
                 GL_FLOAT_VEC2, GL_FLOAT_VEC3, GL_FLOAT_VEC4,
                 GL_FLOAT_MAT2, GL_FLOAT_MAT3, GL_FLOAT_MAT4,
                 GL_FLOAT_MAT2x3, GL_FLOAT_MAT2x4, GL_FLOAT_MAT3x2,
                 GL_FLOAT_MAT3x4, GL_FLOAT_MAT4x2, GL_FLOAT_MAT4x3 -> NUMBER_FORMAT_FLOAT;
            case GL_DOUBLE,
                 GL_DOUBLE_VEC2, GL_DOUBLE_VEC3, GL_DOUBLE_VEC4,
                 GL_DOUBLE_MAT2, GL_DOUBLE_MAT3, GL_DOUBLE_MAT4,
                 GL_DOUBLE_MAT2x3, GL_DOUBLE_MAT2x4, GL_DOUBLE_MAT3x2,
                 GL_DOUBLE_MAT3x4, GL_DOUBLE_MAT4x2, GL_DOUBLE_MAT4x3 -> NUMBER_FORMAT_DOUBLE;
            case GL_UNSIGNED_INT, GL_UNSIGNED_INT_VEC2, GL_UNSIGNED_INT_VEC3, GL_UNSIGNED_INT_VEC4 -> NUMBER_FORMAT_UINT;
            case GL_BOOL, GL_BOOL_VEC2, GL_BOOL_VEC3, GL_BOOL_VEC4 -> NUMBER_FORMAT_BOOL;
            default -> NUMBER_FORMAT_INT;
        };
    }

    public static int size(int type) {
        return switch (type) {
            case GL_FLOAT_VEC2, GL_DOUBLE_VEC2, GL_INT_VEC2, GL_UNSIGNED_INT_VEC2, GL_BOOL_VEC2 -> SIZE_2;
            case GL_FLOAT_VEC3, GL_DOUBLE_VEC3, GL_INT_VEC3, GL_UNSIGNED_INT_VEC3, GL_BOOL_VEC3 -> SIZE_3;
            case GL_FLOAT_VEC4, GL_DOUBLE_VEC4, GL_INT_VEC4, GL_UNSIGNED_INT_VEC4, GL_BOOL_VEC4 -> SIZE_4;
            case GL_FLOAT_MAT2, GL_DOUBLE_MAT2 -> SIZE_MAT2;
            case GL_FLOAT_MAT3, GL_DOUBLE_MAT3 -> SIZE_MAT3;
            case GL_FLOAT_MAT4, GL_DOUBLE_MAT4 -> SIZE_MAT4;
            case GL_FLOAT_MAT2x3, GL_DOUBLE_MAT2x3 -> SIZE_MAT2x3;
            case GL_FLOAT_MAT2x4, GL_DOUBLE_MAT2x4 -> SIZE_MAT2x4;
            case GL_FLOAT_MAT3x2, GL_DOUBLE_MAT3x2 -> SIZE_MAT3x2;
            case GL_FLOAT_MAT3x4, GL_DOUBLE_MAT3x4 -> SIZE_MAT3x4;
            case GL_FLOAT_MAT4x2, GL_DOUBLE_MAT4x2 -> SIZE_MAT4x2;
            case GL_FLOAT_MAT4x3, GL_DOUBLE_MAT4x3 -> SIZE_MAT4x3;
            default -> SIZE_1;
        };
    }

    public static void get(int type, int program, int location, IntBuffer ib, FloatBuffer fb, DoubleBuffer db) {
        switch (numberFormat(type)) {
            case NUMBER_FORMAT_INT, NUMBER_FORMAT_UINT, NUMBER_FORMAT_BOOL -> glGetUniformiv(program, location, ib);
            case NUMBER_FORMAT_FLOAT -> glGetUniformfv(program, location, fb);
            case NUMBER_FORMAT_DOUBLE -> glGetUniformdv(program, location, db);
        }
    }

    public static void set(int type, int ignoredProgram, int location, IntBuffer ib, FloatBuffer fb, DoubleBuffer db) {
        int ignored = switch (numberFormat(type)) {
            case NUMBER_FORMAT_INT, NUMBER_FORMAT_BOOL -> switch (size(type)) {
                case SIZE_1 -> wrapRet(() -> glUniform1iv(location, ib));
                case SIZE_2 -> wrapRet(() -> glUniform2iv(location, ib));
                case SIZE_3 -> wrapRet(() -> glUniform3iv(location, ib));
                case SIZE_4 -> wrapRet(() -> glUniform4iv(location, ib));
                default -> throw new IllegalStateException("Unexpected int or bool size: " + size(type));
            };
            case NUMBER_FORMAT_UINT -> switch (size(type)) {
                case SIZE_1 -> wrapRet(() -> glUniform1uiv(location, ib));
                case SIZE_2 -> wrapRet(() -> glUniform2uiv(location, ib));
                case SIZE_3 -> wrapRet(() -> glUniform3uiv(location, ib));
                case SIZE_4 -> wrapRet(() -> glUniform4uiv(location, ib));
                default -> throw new IllegalStateException("Unexpected uint size: " + size(type));
            };
            case NUMBER_FORMAT_FLOAT -> switch (size(type)) {
                case SIZE_1 -> wrapRet(() -> glUniform1fv(location, fb));
                case SIZE_2 -> wrapRet(() -> glUniform2fv(location, fb));
                case SIZE_3 -> wrapRet(() -> glUniform3fv(location, fb));
                case SIZE_4 -> wrapRet(() -> glUniform4fv(location, fb));
                case SIZE_MAT2 -> wrapRet(() -> glUniformMatrix2fv(location, false, fb));
                case SIZE_MAT3 -> wrapRet(() -> glUniformMatrix3fv(location, false, fb));
                case SIZE_MAT4 -> wrapRet(() -> glUniformMatrix4fv(location, false, fb));
                case SIZE_MAT2x3 -> wrapRet(() -> glUniformMatrix2x3fv(location, false, fb));
                case SIZE_MAT2x4 -> wrapRet(() -> glUniformMatrix2x4fv(location, false, fb));
                case SIZE_MAT3x2 -> wrapRet(() -> glUniformMatrix3x2fv(location, false, fb));
                case SIZE_MAT3x4 -> wrapRet(() -> glUniformMatrix3x4fv(location, false, fb));
                case SIZE_MAT4x2 -> wrapRet(() -> glUniformMatrix4x2fv(location, false, fb));
                case SIZE_MAT4x3 -> wrapRet(() -> glUniformMatrix4x3fv(location, false, fb));
                default -> throw new IllegalStateException("Unexpected float size: " + size(type));
            };
            case NUMBER_FORMAT_DOUBLE -> switch (size(type)) {
                case SIZE_1 -> wrapRet(() -> glUniform1dv(location, db));
                case SIZE_2 -> wrapRet(() -> glUniform2dv(location, db));
                case SIZE_3 -> wrapRet(() -> glUniform3dv(location, db));
                case SIZE_4 -> wrapRet(() -> glUniform4dv(location, db));
                case SIZE_MAT2 -> wrapRet(() -> glUniformMatrix2dv(location, false, db));
                case SIZE_MAT3 -> wrapRet(() -> glUniformMatrix3dv(location, false, db));
                case SIZE_MAT4 -> wrapRet(() -> glUniformMatrix4dv(location, false, db));
                case SIZE_MAT2x3 -> wrapRet(() -> glUniformMatrix2x3dv(location, false, db));
                case SIZE_MAT2x4 -> wrapRet(() -> glUniformMatrix2x4dv(location, false, db));
                case SIZE_MAT3x2 -> wrapRet(() -> glUniformMatrix3x2dv(location, false, db));
                case SIZE_MAT3x4 -> wrapRet(() -> glUniformMatrix3x4dv(location, false, db));
                case SIZE_MAT4x2 -> wrapRet(() -> glUniformMatrix4x2dv(location, false, db));
                case SIZE_MAT4x3 -> wrapRet(() -> glUniformMatrix4x3dv(location, false, db));
                default -> throw new IllegalStateException("Unexpected double size: " + size(type));
            };
            default -> throw new IllegalStateException("Unexpected number format: " + numberFormat(type));
        };
    }

    private static int wrapRet(Runnable code) {
        code.run();
        return 0;
    }

    public static final IntList REGISTRY = IntList.of(
            GL_FLOAT,
            GL_FLOAT_VEC2,
            GL_FLOAT_VEC3,
            GL_FLOAT_VEC4,
            GL_DOUBLE,
            GL_DOUBLE_VEC2,
            GL_DOUBLE_VEC3,
            GL_DOUBLE_VEC4,
            GL_INT,
            GL_INT_VEC2,
            GL_INT_VEC3,
            GL_INT_VEC4,
            GL_UNSIGNED_INT,
            GL_UNSIGNED_INT_VEC2,
            GL_UNSIGNED_INT_VEC3,
            GL_UNSIGNED_INT_VEC4,
            GL_BOOL,
            GL_BOOL_VEC2,
            GL_BOOL_VEC3,
            GL_BOOL_VEC4,
            GL_FLOAT_MAT2,
            GL_FLOAT_MAT3,
            GL_FLOAT_MAT4,
            GL_FLOAT_MAT2x3,
            GL_FLOAT_MAT2x4,
            GL_FLOAT_MAT3x2,
            GL_FLOAT_MAT3x4,
            GL_FLOAT_MAT4x2,
            GL_FLOAT_MAT4x3,
            GL_DOUBLE_MAT2,
            GL_DOUBLE_MAT3,
            GL_DOUBLE_MAT4,
            GL_DOUBLE_MAT2x3,
            GL_DOUBLE_MAT2x4,
            GL_DOUBLE_MAT3x2,
            GL_DOUBLE_MAT3x4,
            GL_DOUBLE_MAT4x2,
            GL_DOUBLE_MAT4x3,
            GL_SAMPLER_1D,
            GL_SAMPLER_2D,
            GL_SAMPLER_3D,
            GL_SAMPLER_CUBE,
            GL_SAMPLER_1D_SHADOW,
            GL_SAMPLER_2D_SHADOW,
            GL_SAMPLER_1D_ARRAY,
            GL_SAMPLER_2D_ARRAY,
            GL_SAMPLER_1D_ARRAY_SHADOW,
            GL_SAMPLER_2D_ARRAY_SHADOW,
            GL_SAMPLER_2D_MULTISAMPLE,
            GL_SAMPLER_2D_MULTISAMPLE_ARRAY,
            GL_SAMPLER_CUBE_SHADOW,
            GL_SAMPLER_BUFFER,
            GL_SAMPLER_2D_RECT,
            GL_SAMPLER_2D_RECT_SHADOW,
            GL_INT_SAMPLER_1D,
            GL_INT_SAMPLER_2D,
            GL_INT_SAMPLER_3D,
            GL_INT_SAMPLER_CUBE,
            GL_INT_SAMPLER_1D_ARRAY,
            GL_INT_SAMPLER_2D_ARRAY,
            GL_INT_SAMPLER_2D_MULTISAMPLE,
            GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY,
            GL_INT_SAMPLER_BUFFER,
            GL_INT_SAMPLER_2D_RECT,
            GL_UNSIGNED_INT_SAMPLER_1D,
            GL_UNSIGNED_INT_SAMPLER_2D,
            GL_UNSIGNED_INT_SAMPLER_3D,
            GL_UNSIGNED_INT_SAMPLER_CUBE,
            GL_UNSIGNED_INT_SAMPLER_1D_ARRAY,
            GL_UNSIGNED_INT_SAMPLER_2D_ARRAY,
            GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE,
            GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY,
            GL_UNSIGNED_INT_SAMPLER_BUFFER,
            GL_UNSIGNED_INT_SAMPLER_2D_RECT);
}
