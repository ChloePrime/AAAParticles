package mod.chloeprime.aaaparticles.forge.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import mod.chloeprime.aaaparticles.forge.AAAParticlesForgeClient;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void onRenderLevelLast(PoseStack pose, float g, long l, boolean bl, Camera camera, GameRenderer arg3, LightTexture arg4, Matrix4f projection, CallbackInfo ci) {
        if (AAAParticlesForgeClient.USE_RENDER_EVENT) {
            return;
        }
        EffekRenderer.onRenderWorldLast(Minecraft.getInstance().getPartialTick(), pose, projection, camera);
    }
}
