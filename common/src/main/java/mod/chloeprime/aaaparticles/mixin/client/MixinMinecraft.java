package mod.chloeprime.aaaparticles.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.platform.Window;
import mod.chloeprime.aaaparticles.client.internal.ReloadTrackable;
import mod.chloeprime.aaaparticles.client.internal.RenderStateCapture;
import mod.chloeprime.aaaparticles.client.registry.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements ReloadTrackable, Executor {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructorFinished(GameConfig gameConfig, CallbackInfo ci) {
        aaa_particles$constructed = true;
    }

    @Inject(method = "resizeDisplay", at = @At("RETURN"))
    private void resizeCapturedDepthBuffer(CallbackInfo ci) {
        if (aaa_particles$constructed) {
            RenderStateCapture.onResize(window.getWidth(), window.getHeight(), ON_OSX);
        }
    }

    @Shadow @Final private Window window;
    @Shadow @Final public static boolean ON_OSX;

    private @Unique boolean aaa_particles$constructed;
    private final @Unique AtomicInteger aaa_particles$myReloadTracker = new AtomicInteger();

    @Inject(
            method = "reloadResourcePacks(ZLnet/minecraft/client/Minecraft$GameLoadCookie;)Ljava/util/concurrent/CompletableFuture;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V"))
    private void beginReload(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        aaa_particles$myReloadTracker.getAndIncrement();
        EffectRegistry.clearAllPlaying();
    }

    @ModifyReturnValue(
            method = "reloadResourcePacks(ZLnet/minecraft/client/Minecraft$GameLoadCookie;)Ljava/util/concurrent/CompletableFuture;",
            at = @At("RETURN"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V")
            ))
    private CompletableFuture<Void> endReload(CompletableFuture<Void> original) {
        return original.thenAccept(_void -> aaa_particles$myReloadTracker.getAndDecrement());
    }

    @Override
    public boolean aaa_particles$isReloading() {
        return aaa_particles$myReloadTracker.get() > 0;
    }
}
