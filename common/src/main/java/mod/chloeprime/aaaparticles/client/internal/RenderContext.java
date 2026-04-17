package mod.chloeprime.aaaparticles.client.internal;

import mod.chloeprime.aaaparticles.PlatformMethods;

public class RenderContext {
    private static final PlatformMethods P = PlatformMethods.get();
    public static final boolean HAS_IRIS = P.isModLoaded("iris") || P.isModLoaded("oculus");
    public static final boolean HAS_SODIUM = P.isModLoaded("sodium");
    public static final boolean ON_FABRIC = P.isFabric();

    public static boolean renderLevelDeferred() {
        // Should always be false on 26.1
        return false;
    }

    public static boolean renderLevelAfterHand() {
        return ON_FABRIC && HAS_SODIUM && !renderHandDeferred();
    }

    public static boolean renderHandDeferred() {
        return isIrisShaderEnabled();
    }

    public static boolean captureHandDepth() {
        return !HAS_IRIS || !isIrisShaderEnabled();
    }

    public static boolean isIrisShaderEnabled() {
        return HAS_IRIS && IrisProxy.isIrisShaderEnabled();
    }

    private RenderContext() {}
}
