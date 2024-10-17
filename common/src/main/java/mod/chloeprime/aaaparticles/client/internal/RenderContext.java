package mod.chloeprime.aaaparticles.client.internal;

import static dev.architectury.platform.Platform.isFabric;
import static dev.architectury.platform.Platform.isModLoaded;

public class RenderContext {
    public static final boolean HAS_IRIS = isModLoaded("iris") || isModLoaded("oculus");
    public static final boolean HAS_SODIUM = isModLoaded("sodium");
    public static final boolean ON_FABRIC = isFabric();

    public static boolean renderLevelDeferred() {
        return !HAS_IRIS || ON_FABRIC;
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
