package mod.chloeprime.aaaparticles.mixin;

import net.minecraft.util.FileUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = FileUtil.class, priority = Integer.MAX_VALUE)
public class MixinFileUtil {
    /**
     * @author ChloePrime
     * @reason Make Effekseer effects easier to import
     */
    @Overwrite
    private static boolean containsAllowedCharactersOnly(String segment) {
        return !"DUMMY".equals(segment);
    }
}
