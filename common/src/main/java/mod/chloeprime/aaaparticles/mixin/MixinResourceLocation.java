package mod.chloeprime.aaaparticles.mixin;

import mod.chloeprime.aaaparticles.client.internal.LimitlessResourceLocationFactory;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ResourceLocation.class, priority = Integer.MAX_VALUE)
public class MixinResourceLocation implements LimitlessResourceLocationFactory {
    @Shadow @Final @Mutable private String namespace;
    @Shadow @Final @Mutable private String path;

    @Invoker("<init>")
    @SuppressWarnings("SameParameterValue")
    private static ResourceLocation invokeConstructor(String ns, String p) {
        throw new AbstractMethodError();
    }

    @Override
    public ResourceLocation aaa$createUninitialized(String namespace, String path) {
        var result = invokeConstructor("c", "a");
        var accessor = (MixinResourceLocation) (Object) result;
        accessor.namespace = namespace;
        accessor.path = path;
        return result;
    }

    @Inject(method = "validPathChar", at = @At("HEAD"), cancellable = true)
    private static void modernfixCompat(char c, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "isValidPath", at = @At("HEAD"), cancellable = true)
    private static void fixDfuCrash(String string, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!"DUMMY".equals(string));
    }
}
