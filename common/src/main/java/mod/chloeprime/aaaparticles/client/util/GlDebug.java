package mod.chloeprime.aaaparticles.client.util;

import dev.architectury.platform.Platform;
import org.lwjgl.opengl.GL43C;

import java.util.function.Supplier;

public class GlDebug {
    public static final boolean ENABLED = Platform.isDevelopmentEnvironment() || Boolean.getBoolean("mod.chloeprime.aaaparticles.enableGlDebugGroups");

    public static void pushDebugGroup(int id, Supplier<? extends CharSequence> message) {
        if (ENABLED) {
            AppleFriendlyGL.glPushDebugGroup(GL43C.GL_DEBUG_SOURCE_APPLICATION, id, message.get());
        }
    }

    public static void popDebugGroup() {
        if (ENABLED) {
            AppleFriendlyGL.glPopDebugGroup();
        }
    }
}
