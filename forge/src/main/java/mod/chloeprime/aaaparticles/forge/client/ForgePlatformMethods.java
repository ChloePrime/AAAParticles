package mod.chloeprime.aaaparticles.forge.client;

import com.google.auto.service.AutoService;
import com.google.common.base.Suppliers;
import mod.chloeprime.aaaparticles.PlatformMethods;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

import java.util.function.Supplier;

@AutoService(PlatformMethods.class)
public class ForgePlatformMethods implements PlatformMethods {
    private static final Supplier<Boolean> IS_DATA = Suppliers.memoize(DatagenModLoader::isRunningDataGen);

    private static final Dist DIST = FMLLoader.getCurrent().getDist();

    @Override
    public boolean isClientDist() {
        return DIST.isClient();
    }

    @Override
    public boolean isDatagen() {
        return IS_DATA.get();
    }
}
