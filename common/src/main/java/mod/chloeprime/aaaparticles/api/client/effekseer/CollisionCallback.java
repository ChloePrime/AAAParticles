package mod.chloeprime.aaaparticles.api.client.effekseer;

import mod.chloeprime.aaaparticles.client.internal.CollisionCallbackSupport;
import org.joml.Vector3d;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

import javax.annotation.Nonnull;

import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.libffi.LibFFI.*;

@FunctionalInterface
public interface CollisionCallback extends CallbackI {
    /**
     * Perform a ray cast.
     *
     * @param start Start position of the ray. The value is reused, so do not store its reference.
     * @param end End position of the ray. The value is reused, so do not store its reference.
     * @param outCollisionPosition Collision position output (implementers should mutate this)
     * @param outCollisionNormal Collision normal output (implementers should mutate this)
     * @return whether the ray cast hits anything
     * @since 2.2.0
     */
    boolean trace(Vector3d start, Vector3d end, Vector3d outCollisionPosition, Vector3d outCollisionNormal);

    FFICIF CIF = apiCreateCIF(
            apiStdcall(),
            ffi_type_uint8,
            ffi_type_double, ffi_type_double, ffi_type_double, ffi_type_double, ffi_type_double, ffi_type_double, ffi_type_pointer
    );

    @Override
    default @Nonnull FFICIF getCallInterface() {
        return CIF;
    }

    @Override
    @SuppressWarnings("PointlessArithmeticExpression")
    default void callback(long ret, long args) {
        boolean result = invoke(
                memGetDouble(memGetAddress(args + 0 * Double.BYTES)),
                memGetDouble(memGetAddress(args + 1 * Double.BYTES)),
                memGetDouble(memGetAddress(args + 2 * Double.BYTES)),
                memGetDouble(memGetAddress(args + 3 * Double.BYTES)),
                memGetDouble(memGetAddress(args + 4 * Double.BYTES)),
                memGetDouble(memGetAddress(args + 5 * Double.BYTES)),
                memGetAddress(memGetAddress(args + 6 * Double.BYTES))
        );
        apiClosureRet(ret, result);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    default boolean invoke(
            double startX, double startY, double startZ,
            double endX, double endY, double endZ,
            @NativeType("double[6]") long output
    ) {
        var buf = CollisionCallbackSupport.getVectorBuffer();
        if (buf.length != 4) {
            throw new IllegalStateException();
        }
        buf[0].set(startX, startY, startZ);
        buf[1].set(endX, endY, endZ);
        var ret = trace(buf[0], buf[1], buf[2], buf[3]);
        memPutDouble(output + 0 * Double.BYTES, buf[2].x());
        memPutDouble(output + 1 * Double.BYTES, buf[2].y());
        memPutDouble(output + 2 * Double.BYTES, buf[2].z());
        memPutDouble(output + 3 * Double.BYTES, buf[3].x());
        memPutDouble(output + 4 * Double.BYTES, buf[3].y());
        memPutDouble(output + 5 * Double.BYTES, buf[3].z());
        return ret;
    }
}
