package mod.chloeprime.aaaparticles.forge.client;

import com.google.auto.service.AutoService;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import mod.chloeprime.aaaparticles.client.ClientPlatformMethods;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.neoforge.client.textures.UnitTextureAtlasSprite;

@AutoService(ClientPlatformMethods.class)
public class ForgeClientPlatformMethods implements ClientPlatformMethods {
    @Override
    public int getDepthFormat(RenderTarget fb) {
        return fb.useStencil ? 2 : 1;
    }

    @Override
    public TextureAtlasSprite getPlaceholderAtlasSprite26_1() {
        return UnitTextureAtlasSprite.INSTANCE;
    }

    @Override
    public boolean isStencilEnabled26_1(RenderTarget target) {
        return target.useStencil;
    }

    @Override
    public RenderTarget newTextureTarget26_1(String label, int w, int h, boolean depth, boolean stencil) {
        return new TextureTarget(label, w, h, depth, stencil);
    }
}
