package mod.chloeprime.aaaparticles.common.util;

import net.minecraft.client.Minecraft;

public class DeltaTracker {
    private static final Minecraft GAME = Minecraft.getInstance();
    private static long lastTick;
    private static float lastPartial;
    private static float delta;

    public static float getDeltaTime() {
        return delta;
    }

    public static void run() {
        var level = GAME.level;
        if (level == null) {
            delta = 0;
            return;
        }
        var curTick = level.getGameTime();
        var curPartial = GAME.getTimer().getGameTimeDeltaPartialTick(true);
        // 超过 5 秒则判定为时间异常，
        // 为防止 Effekseer 引擎瞬间计算大量动画时间，此处重置 delta
        if (Math.abs(curTick - lastTick) > 20 * 5) {
            delta = 0;
        } else {
            delta = ((curTick - lastTick) + (curPartial - lastPartial)) / 20F;
        }
        lastTick = curTick;
        lastPartial = curPartial;
    }
}
