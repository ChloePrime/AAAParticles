package mod.chloeprime.aaaparticles.forge.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraftforge.client.MinecraftForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void onRenderLevelLast(PoseStack pose, float g, long m, boolean bl, Camera arg2, GameRenderer arg3, LightTexture arg4, Matrix4f arg5, CallbackInfo ci) {
        EffekRenderer.onRenderWorldLast(MinecraftForgeClient.getPartialTick(), pose, arg5, arg2);
    }
}
