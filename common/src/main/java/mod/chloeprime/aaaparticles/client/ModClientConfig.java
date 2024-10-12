package mod.chloeprime.aaaparticles.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue EFFEK_LIGHTNING_IN_PRODUCTION_ENVIRONMENT;

    static {
        var builder = new ForgeConfigSpec.Builder();

        EFFEK_LIGHTNING_IN_PRODUCTION_ENVIRONMENT = builder
                .comment("""
                        Replace the visual of lighting with an effekseer effect,
                        in production environment.""")
                .define("effek_lightning_in_production_environment", false);

        SPEC = builder.build();
    }

}
