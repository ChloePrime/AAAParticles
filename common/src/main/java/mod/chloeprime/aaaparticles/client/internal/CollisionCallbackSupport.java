package mod.chloeprime.aaaparticles.client.internal;

import com.google.common.collect.MapMaker;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.api.client.effekseer.CollisionCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class CollisionCallbackSupport {
    public static Vector3d[] getVectorBuffer() {
        return VEC_BUFFER.get();
    }

    public static boolean trace(Vector3d start, Vector3d end, Vector3d outCollisionPosition, Vector3d outCollisionNormal) {
        try {
            return trace0(start, end, outCollisionPosition, outCollisionNormal);
        } catch (Exception ex) {
            outCollisionPosition.set(start);
            outCollisionNormal.set(0, 1, 0);
            try {
                logCollisionError(ex);
            } catch (Exception logError) {
                AAAParticles.LOGGER.error("Failed to log collision error");
                AAAParticles.LOGGER.error("Collision Error:", ex);
                AAAParticles.LOGGER.error("Logging Error:", ex);
            }
            return false;
        }
    }

    private static boolean trace0(Vector3d start, Vector3d end, Vector3d outCollisionPosition, Vector3d outCollisionNormal) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return false;
        }
        var mojStart = new Vec3(start.x(), start.y(), start.z());
        var mojEnd = new Vec3(end.x(), end.y(), end.z());
        var marker = Impl.CLIP_MARKERS.computeIfAbsent(level, lvl -> new Marker(EntityType.MARKER, level));
        marker.setPos(mojStart);

        var clipContext = new ClipContext(mojStart, mojEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, marker);
        var mojResult = level.clip(clipContext);
        if (mojResult.getType() == HitResult.Type.MISS) {
            outCollisionPosition.set(end);
            outCollisionNormal.set(0, 1, 0);
            return false;
        } else {
            var pos = mojResult.getLocation();
            var normal = mojResult.getDirection().getNormal();
            outCollisionPosition.set(pos.x(), pos.y(), pos.z());
            outCollisionNormal.set(normal.getX(), normal.getY(), normal.getZ());
            return true;
        }
    }

    private static final ThreadLocal<Vector3d[]> VEC_BUFFER = ThreadLocal.withInitial(CollisionCallbackSupport::newVectorBuffer);
    private static final Set<StackTraceElement> LOGGED_ERRORS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static Vector3d[] newVectorBuffer() {
        int count = 4;
        var buff = new Vector3d[count];
        for (int i = 0; i < count; i++) {
            buff[i] = new Vector3d();
        }
        return buff;
    }

    private static void logCollisionError(Throwable ex) {
        var stacktrace = ex.getStackTrace();
        if (stacktrace.length == 0) {
            AAAParticles.LOGGER.error("Error computing particle collision", ex);
            return;
        }
        if (LOGGED_ERRORS.add(stacktrace[0])) {
            AAAParticles.LOGGER.error("Error computing particle collision", ex);
        } else {
            AAAParticles.LOGGER.error("Error computing particle collision: {}", ex.getMessage());
        }
    }

    public static final class Impl {
        public static final Map<Level, Marker> CLIP_MARKERS = new MapMaker().weakKeys().makeMap();
        public static final CollisionCallback DEFAULT_TRACER = CollisionCallbackSupport::trace;

        private Impl() {
        }
    }

    private CollisionCallbackSupport() {
    }
}
