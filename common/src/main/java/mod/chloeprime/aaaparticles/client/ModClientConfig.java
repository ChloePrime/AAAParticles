package mod.chloeprime.aaaparticles.client;

import dev.architectury.platform.Platform;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue EFFEK_LIGHTNING_IN_PRODUCTION_ENVIRONMENT;

    public static boolean isLightningEnabled() {
        return IS_DEV_ENV || EFFEK_LIGHTNING_IN_PRODUCTION_ENVIRONMENT.get();
    }

    static {
        var builder = new ForgeConfigSpec.Builder();

        EFFEK_LIGHTNING_IN_PRODUCTION_ENVIRONMENT = builder
                .comment("""
                        Replace the visual of lighting with an effekseer effect,
                        in production environment.""")
                .define("effek_lightning_in_production_environment", false);

        SPEC = builder.build();
    }

    private static final boolean IS_DEV_ENV = Platform.isDevelopmentEnvironment();
}
