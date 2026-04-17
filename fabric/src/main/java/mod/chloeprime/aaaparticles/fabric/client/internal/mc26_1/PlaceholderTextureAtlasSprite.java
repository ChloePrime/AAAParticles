package mod.chloeprime.aaaparticles.fabric.client.internal.mc26_1;

import com.mojang.blaze3d.platform.NativeImage;
import mod.chloeprime.aaaparticles.AAAParticles;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;

public class PlaceholderTextureAtlasSprite extends TextureAtlasSprite {
    public static final Identifier LOCATION = AAAParticles.loc("placeholder_atlas");
    public static final PlaceholderTextureAtlasSprite INSTANCE = new PlaceholderTextureAtlasSprite();

    private PlaceholderTextureAtlasSprite() {
        super(
                LOCATION,
                new SpriteContents(LOCATION, new FrameSize(1, 1), new NativeImage(1, 1, true)),
                1, 1, 0, 0, 0);
    }

    @Override
    public float getU(float offset) {
        return offset;
    }

    @Override
    public float getV(float offset) {
        return offset;
    }
}