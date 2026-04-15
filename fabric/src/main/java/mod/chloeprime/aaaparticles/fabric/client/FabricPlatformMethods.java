package mod.chloeprime.aaaparticles.fabric.client;

import com.google.auto.service.AutoService;
import com.google.common.base.Suppliers;
import mod.chloeprime.aaaparticles.PlatformMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Supplier;

@AutoService(PlatformMethods.class)
public class FabricPlatformMethods implements PlatformMethods {
    private static final Supplier<Boolean> IS_DATAGEN = Suppliers.memoize(() -> System.getProperty("fabric-api.datagen") != null);

    @Override
    public boolean isClientDist() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public boolean isDatagen() {
        return IS_DATAGEN.get();
    }
}
