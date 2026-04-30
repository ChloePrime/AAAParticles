package mod.chloeprime.aaaparticles.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class AAAClientConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue GC_ENABLE;

    static {
        var builder = new ModConfigSpec.Builder();

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
