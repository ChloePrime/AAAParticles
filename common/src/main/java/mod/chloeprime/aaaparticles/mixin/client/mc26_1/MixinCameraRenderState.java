package mod.chloeprime.aaaparticles.mixin.client.mc26_1;

import mod.chloeprime.aaaparticles.client.internal.mc26_1.EnhancedCameraRenderState;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CameraRenderState.class)
public class MixinCameraRenderState implements EnhancedCameraRenderState {
    private final @Unique MutableObject<Camera> aaa_particles$camera = new MutableObject<>();

    @Override
    public MutableObject<Camera> aaa_particles$getCamera() {
        return aaa_particles$camera;
    }
}
