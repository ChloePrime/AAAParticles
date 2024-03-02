package mod.chloeprime.aaaparticles.common.util;

import net.minecraft.resources.ResourceLocation;

/**
 * 仍然需要 {@link mod.chloeprime.aaaparticles.mixin.MixinResourceLocation} 才能完全去除 Path 的有效性验证
 */
public class LimitlessResourceLocation extends ResourceLocation {
    public LimitlessResourceLocation(String namespace, String path) {
        super(namespace, path, null);
    }
}
