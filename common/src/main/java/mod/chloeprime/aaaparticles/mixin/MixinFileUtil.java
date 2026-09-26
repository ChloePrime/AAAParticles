package mod.chloeprime.aaaparticles.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.FileUtil;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = FileUtil.class, priority = Integer.MAX_VALUE)
public class MixinFileUtil {
    @WrapMethod(method = "isValidStrictPathSegment")
    private static boolean disableStrictPathSegmentValidation(String path, Operation<Boolean> original) {
        return path.startsWith("effeks/") || original.call(path);
    }
}
