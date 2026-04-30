package mod.chloeprime.aaaparticles.client;

import net.minecraftforge.common.ForgeConfigSpec;

public final class AAAClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue GC_ENABLE;

    static {
        var builder = new ForgeConfigSpec.Builder();

        builder.push("gc");
        {
            GC_ENABLE = builder
                    .comment("""
                            If true, enable auto releasing effeks when
                            the OS has high memory usage.""")
                    .define("enabled", false);
        }
        builder.pop();

        SPEC = builder.build();
    }

    private AAAClientConfig() {
    }
}
