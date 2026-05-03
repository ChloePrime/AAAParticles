package mod.chloeprime.aaaparticles.mixin.client;

import mod.chloeprime.aaaparticles.api.client.EffectDefinition;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ClientShutdownHookMixin {
    @Inject(
            method = "close",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;shutdownExecutors()V"))
    private void onMinecraftShutdown(CallbackInfo ci) {
        EffectDefinition.shutdown();
    }
}
