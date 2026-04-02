package mod.chloeprime.aaaparticles.client.util;

import org.lwjgl.opengl.GL20C;

import java.nio.ByteBuffer;

public class ActiveUniform {
    private final int[] lengthPtr = newPointer();
    private final int[] sizePtr = newPointer();
    private final int[] typePtr = newPointer();
    private static final ActiveUniform INSTANCE = new ActiveUniform();

    public static ActiveUniform fetchAndGetInstance(int program, int index, ByteBuffer name) {
        INSTANCE.fetch(program, index, name);
        return INSTANCE;
    }

    public static void updateAndGetInstance(int length, int size, int type) {
        INSTANCE.lengthPtr[0] = length;
        INSTANCE.sizePtr[0] = size;
        INSTANCE.typePtr[0] = type;
    }

    public int length() {
        return lengthPtr[0];
    }

    public int size() {
        return sizePtr[0];
    }

    public int type() {
        return typePtr[0];
    }

    public void fetch(int program, int index, ByteBuffer name) {
        GL20C.glGetActiveUniform(program, index, lengthPtr, sizePtr, typePtr, name);
    }

    private static int[] newPointer() {
        return new int[]{0};
    }
}
