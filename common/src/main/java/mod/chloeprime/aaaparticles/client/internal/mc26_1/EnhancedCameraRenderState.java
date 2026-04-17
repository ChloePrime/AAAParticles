package mod.chloeprime.aaaparticles.client.internal.mc26_1;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Objects;

public interface EnhancedCameraRenderState {
    static Camera getCamera(CameraRenderState state) {
        return Objects.requireNonNull(((EnhancedCameraRenderState) state).aaa_particles$getCamera().get(), "Unextracted camera render state");
    }

    MutableObject<Camera> aaa_particles$getCamera();
}
