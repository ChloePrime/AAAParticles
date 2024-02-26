package mod.chloeprime.aaaparticles.common.util;

import net.minecraft.resources.ResourceLocation;

/**
 * Implementation moved to {@link mod.chloeprime.aaaparticles.mixin.MixinResourceLocation}
 */
public class LimitlessResourceLocation extends ResourceLocation {
    public LimitlessResourceLocation(String namespace, String path) {
        super(namespace, path);
    }
}
