package mod.chloeprime.aaaparticles.client.util;


import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;

public class GlErrorSilencer {
    private static final boolean ENABLED = !Boolean.getBoolean("mod.chloeprime.aaaparticles.keepAllGlDebugMessages");
    private static final ThreadLocal<Int2LongMap> ANGRY_MAP = ThreadLocal.withInitial(Int2LongOpenHashMap::new);
    private static final long CALM_DELAY = 5 * 1000;

    public static void trySilence(int k, Runnable canceller) {
        if (!ENABLED) {
            return;
        }
        var angryMap = ANGRY_MAP.get();
        var silenceEndTime = angryMap.get(k);
        var now = System.currentTimeMillis();
        if (now < silenceEndTime) {
            canceller.run();
        }
        angryMap.put(k, now + CALM_DELAY);
    }
}
