package mod.chloeprime.aaaparticles.common.util;

import net.minecraft.resources.ResourceLocation;

/**
 * 餓띸꽫��誤� {@link mod.chloeprime.aaaparticles.mixin.MixinResourceLocation} �뎺�꺗若뚦뀲�렮�솮 Path �쉪�쐣�븞�㏝챿瑥�
 */
public class LimitlessResourceLocation extends ResourceLocation {
    public LimitlessResourceLocation(String namespace, String path) {
        super(namespace, path);
    }
}
