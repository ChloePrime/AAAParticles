package mod.chloeprime.aaaparticles.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import mod.chloeprime.aaaparticles.PlatformMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.function.Consumer;

public interface ClientPlatformMethods {
    static ClientPlatformMethods get() {
        if (PlatformMethods.get().isDedicatedServerDist()) {
            throw new IllegalStateException("Trying to load client-only platform methods on dedicated server");
        }
        return ClientPlatformMethodsImpl.INSTANCE;
    }

    int getDepthFormat(RenderTarget fb);
    void addClientPostTickCallback(Consumer<Minecraft> action);

    TextureAtlasSprite getPlaceholderAtlasSprite26_1();
    boolean isStencilEnabled26_1(RenderTarget target);
    RenderTarget newTextureTarget26_1(String label, int w, int h, boolean depth, boolean stencil);
}
