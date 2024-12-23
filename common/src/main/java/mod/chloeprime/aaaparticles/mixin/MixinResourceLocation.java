package mod.chloeprime.aaaparticles.mixin;

import mod.chloeprime.aaaparticles.client.internal.LimitlessResourceLocationFactory;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ResourceLocation.class, priority = Integer.MAX_VALUE)
public class MixinResourceLocation implements LimitlessResourceLocationFactory {
    @Shadow @Final @Mutable private String namespace;
    @Shadow @Final @Mutable private String path;

    @Invoker("<init>")
    @SuppressWarnings("SameParameterValue")
    private static ResourceLocation invokeConstructor(String ns, String p) {
        throw new AbstractMethodError();
    }

    @Override
    public ResourceLocation aaa$createUninitialized(String namespace, String path) {
        var result = invokeConstructor("c", "a");
        var accessor = (MixinResourceLocation) (Object) result;
        accessor.namespace = namespace;
        accessor.path = path;
        return result;
    }

    /**
     * @author ChloePrime
     * @reason Make Effekseer effects easier to import
     */
    @Overwrite
    public static boolean isValidPath(String path) {
        return aaa_particles$fixDfuCrash(path);
    }

    /**
     * @author ChloePrime
     * @reason Make Effekseer effects easier to import
     */
    @Overwrite
    public static boolean isValidNamespace(String namespace) {
        return true;
    }

    /**
     * @author ChloePrime
     * @reason ModernFix compat
     */
    @Overwrite
    public static boolean validNamespaceChar(char c) {
        return true;
    }

    /**
     * @author ChloePrime
     * @reason ModernFix compat
     */
    @Overwrite
    public static boolean validPathChar(char c) {
        return true;
    }

    @Unique
    private static boolean aaa_particles$fixDfuCrash(String string) {
        return !"DUMMY".equals(string);
    }
}
