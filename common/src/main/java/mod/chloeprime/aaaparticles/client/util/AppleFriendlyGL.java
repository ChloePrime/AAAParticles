package mod.chloeprime.aaaparticles.client.util;

import net.minecraft.util.Util;
import org.lwjgl.opengl.GL43C;

public class AppleFriendlyGL {
    private static final boolean ON_OSX = Util.getPlatform() == Util.OS.OSX;

    public static void glPushDebugGroup(int source, int id, CharSequence message) {
        if (ON_OSX) {
            return;
        }
        GL43C.glPushDebugGroup(source, id, message);
    }

    public static void glPopDebugGroup() {
        if (ON_OSX) {
            return;
        }
        GL43C.glPopDebugGroup();
    }
}
