package mod.chloeprime.aaaparticles.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import mod.chloeprime.aaaparticles.common.util.LimitlessResourceLocation;
import net.minecraft.CharPredicate;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Util.class)
public class MixinMojangUtil {
    @WrapMethod(method = "sanitizeName")
    private static String sanitizeNameWithPathCharValidationEnabled(String fileName, CharPredicate characterValidator, Operation<String> original) {
        return LimitlessResourceLocation.withPathCharValidationEnabled(() -> original.call(fileName, characterValidator));
    }
}
