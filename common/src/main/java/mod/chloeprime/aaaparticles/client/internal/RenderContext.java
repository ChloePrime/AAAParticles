package mod.chloeprime.aaaparticles.client.internal;

import static dev.architectury.platform.Platform.isFabric;
import static dev.architectury.platform.Platform.isModLoaded;

public class RenderContext {
    public static final boolean IRIS_MODE = isModLoaded("iris") || isModLoaded("oculus");
    public static final boolean ON_FABRIC = isFabric();

    public static boolean renderLevelDeferred() {
        return !IRIS_MODE || ON_FABRIC;
    }

    public static boolean renderHandDeferred() {
        return !IRIS_MODE || (ON_FABRIC || isIrisShaderEnabled());
    }

    public static boolean captureHandDepth() {
        return !IRIS_MODE || !isIrisShaderEnabled();
    }

    public static boolean isIrisShaderEnabled() {
        return IRIS_MODE && IrisProxy.isIrisShaderEnabled();
    }

    private RenderContext() {}
}
