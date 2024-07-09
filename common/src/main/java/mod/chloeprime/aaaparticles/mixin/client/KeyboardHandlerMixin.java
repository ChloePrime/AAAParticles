package mod.chloeprime.aaaparticles.mixin.client;

import mod.chloeprime.aaaparticles.client.Debug;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("TAIL"))
    private void press(long l, int i, int j, int k, int m, CallbackInfo ci) {
        if (l == this.minecraft.getWindow().getWindow()) {
            Debug.keyPressed0(this.minecraft, i, j, k, m);
        }
    }
}
