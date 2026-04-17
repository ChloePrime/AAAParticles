package mod.chloeprime.aaaparticles.fabric.client;

import com.google.auto.service.AutoService;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import mod.chloeprime.aaaparticles.client.ClientPlatformMethods;
import mod.chloeprime.aaaparticles.fabric.client.internal.mc26_1.PlaceholderTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

@AutoService(ClientPlatformMethods.class)
public class FabricClientPlatformMethods implements ClientPlatformMethods {
    @Override
    public int getDepthFormat(RenderTarget fb) {
        return 1;
    }

    @Override
    public boolean isStencilEnabled26_1(RenderTarget target) {
        return false;
    }

    @Override
    public TextureAtlasSprite getPlaceholderAtlasSprite26_1() {
        return PlaceholderTextureAtlasSprite.INSTANCE;
    }

    @Override
    public RenderTarget newTextureTarget26_1(String label, int w, int h, boolean depth, boolean stencil) {
        return new TextureTarget(label, w, h, depth);
    }
}
