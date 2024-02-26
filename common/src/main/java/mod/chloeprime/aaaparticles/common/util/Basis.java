package mod.chloeprime.aaaparticles.common.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record Basis(
        Vec3 row0,
        Vec3 row1,
        Vec3 row2,
        Vec3 x,
        Vec3 y,
        Vec3 z
) {
    public static Basis factory(Vec3 row0, Vec3 row1, Vec3 row2) {
        var x = new Vec3(row0.x, row1.x, row2.x);
        var y = new Vec3(row0.y, row1.y, row2.y);
        var z = new Vec3(row0.z, row1.z, row2.z);
        return new Basis(row0, row1, row2, x, y, z);
    }

    /**
     * Basis from x, y, z
     * Godot's basis constructor
     */
    public static Basis fromVectors(Vec3 x, Vec3 y, Vec3 z) {
        var row0 = new Vec3(x.x, y.x, z.x);
        var row1 = new Vec3(x.y, y.y, z.y);
        var row2 = new Vec3(x.z, y.z, z.z);
        return new Basis(row0, row1, row2, x, y, z);
    }

    public Vec3 toLocal(Vec3 global) {
        return new Vec3(global.dot(x), global.dot(y), global.dot(z));
    }

    public Vec3 toGlobal(Vec3 local) {
        return new Vec3(
                x.x * local.x + y.x * local.y + z.x * local.z,
                x.y * local.x + y.y * local.y + z.y * local.z,
                x.z * local.x + y.z * local.y + z.z * local.z
        );
    }

    public static Basis fromBodyRotation(float yRot) {
        var z = getBodyFront(yRot).reverse();
        return fromVectors(getBodyX(z), UP, z);
    }

    public static Basis fromEntityBody(Entity entity) {
        return fromBodyRotation(entity.getYRot());
    }

    public static Basis fromEuler(Vec3 euler) {
        var sin = Math.sin(euler.x);
        var cos = Math.cos(euler.x);
        var xmat = fromVectors(
                new Vec3(1, 0, 0),
                new Vec3(0, cos, sin),
                new Vec3(0, -sin, cos)
        );

        sin = Math.sin(euler.y);
        cos = Math.cos(euler.y);
        var ymat = fromVectors(
                new Vec3(cos, 0, -sin),
                new Vec3(0, 1, 0),
                new Vec3(sin, 0, cos)
        );

        sin = Math.sin(euler.z);
        cos = Math.cos(euler.z);
        var zmat = fromVectors(
                new Vec3(cos, sin, 0),
                new Vec3(-sin, cos, 0),
                new Vec3(0, 0, 1)
        );
        return mul(mul(ymat, xmat), zmat);
    }

    public static Basis mul(Basis left, Basis right) {
        return factory(
                new Vec3(right.tDotX(left.row0), right.tDotY(left.row0), right.tDotZ(left.row0)),
                new Vec3(right.tDotX(left.row1), right.tDotY(left.row1), right.tDotZ(left.row1)),
                new Vec3(right.tDotX(left.row2), right.tDotY(left.row2), right.tDotZ(left.row2))
        );
    }

    public double tDotX(Vec3 with) {
        return row0.x * with.x + row1.x * with.y + row2.x * with.z;
    }

    public double tDotY(Vec3 with) {
        return row0.y * with.x + row1.y * with.y + row2.y * with.z;
    }

    public double tDotZ(Vec3 with) {
        return row0.z * with.x + row1.z * with.y + row2.z * with.z;
    }

    private static final Vec3 UP = new Vec3(0, 1, 0);

    private static Vec3 getBodyFront(float yRot) {
        return new Vec3(-Mth.sin((float)(Math.toRadians(yRot))), 0, Mth.cos((float)(Math.toRadians(yRot))));
    }

    private static Vec3 getBodyX(Vec3 z) {
        return new Vec3(z.z, 0, -z.x);
    }
}
