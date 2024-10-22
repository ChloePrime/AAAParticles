package mod.chloeprime.aaaparticles.client.installer.forge;

import com.google.common.base.Suppliers;
import net.neoforged.fml.loading.FMLLoader;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class NativePlatformImpl {
    private static final Supplier<Boolean> IS_DATA = Suppliers.memoize(
            () -> {
                var name = FMLLoader.launcherHandlerName();
                return "forgedata".equals(name) ||
                        "forgedatadev".equals(name) ||
                        "forgedatauserdev".equals(name);
            });

    public static boolean isDataGen() {
        return IS_DATA.get();
    }
}
