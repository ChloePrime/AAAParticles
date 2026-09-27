package mod.chloeprime.aaaparticles.forge.client;

import com.google.auto.service.AutoService;
import com.google.common.base.Suppliers;
import mod.chloeprime.aaaparticles.PlatformMethods;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

import java.util.function.Supplier;

@AutoService(PlatformMethods.class)
public class ForgePlatformMethods implements PlatformMethods {
    private static final Supplier<Boolean> IS_DATA = Suppliers.memoize(DatagenModLoader::isRunningDataGen);
    private static final Dist DIST = FMLLoader.getCurrent().getDist();

    @Override
    public boolean isFabric() {
        return false;
    }

    @Override
    public boolean isForge() {
        return true;
    }

    @Override
    public boolean isClientDist() {
        return DIST.isClient();
    }

    @Override
    public boolean isDatagen() {
        return IS_DATA.get();
    }

    @Override
    public int isModLoaded(String modid) {
        var modlist = ModList.get();
        if (modlist == null) {
            return FMLLoader.getCurrent().getLoadingModList().getAllModFiles()
                    .stream()
                    .anyMatch(mod -> modid.equals(mod.getId()))
                    ? 1 : 0;
        }
        return modlist.isLoaded(modid) ? 1 : 0;
    }
}
