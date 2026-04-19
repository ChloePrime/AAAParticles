package mod.chloeprime.aaaparticles.api.common;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import mod.chloeprime.aaaparticles.api.client.EffectRegistry;
import mod.chloeprime.aaaparticles.api.client.EffectHolder;
import mod.chloeprime.aaaparticles.common.network.S2CAddParticle;
import mod.chloeprime.aaaparticles.common.util.Basis;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticleEmitterInfo implements Cloneable {
    public static class FlagMask {
        public static final int HAS_EMITTER = 1;
        public static final int IS_POSITION_SET = 2;
        public static final int IS_ROTATION_SET = 4;
        public static final int IS_SCALE_SET = 8;
        public static final int IS_SPEED_SET = 512;
        public static final int HAS_BOUND_ENTITY = 16;
        public static final int IS_ENTITY_SPACE_RELATIVE_POSITION_SET = 32;
        public static final int USE_ENTITY_HEAD_SPACE = 64;
        public static final int USE_ENTITY_VELOCITY_AS_ROTATION = 1024;
        public static final int HAS_PARAMETERS = 128;
        public static final int HAS_TRIGGERS = 256;
    }

    /**
     * Create a packet when on logic server,
     * with an anonymous emitter that can't be referenced later.
     */
    public static ParticleEmitterInfo create(Level level, ResourceLocation location) {
        return level.isClientSide()
                ? new ParticleEmitterInfo(location)
                : new S2CAddParticle(location);
    }

    /**
     * Create a packet when on logic server,
     * with a named emitter that can be referenced later.
     */
    public static ParticleEmitterInfo create(Level level, ResourceLocation location, ResourceLocation emitterName) {
        return level.isClientSide()
                ? new ParticleEmitterInfo(location, emitterName)
                : new S2CAddParticle(location, emitterName);
    }

    public final ResourceLocation effek;
    public final ResourceLocation emitter;
    protected int flags;
    protected double x, y, z;
    protected float rotX, rotY, rotZ;
    protected float scaleX = 1, scaleY = 1, scaleZ = 1;
    protected float speed = 1;
    protected double esX, esY, esZ;
    protected int boundEntity;
    protected final List<DynamicParameter> parameters = new ArrayList<>();
    protected final IntList triggers = new IntArrayList();
    private static final Vec3 VEC3_ONES = new Vec3(1, 1, 1);

    /**
     * @see #create(Level, ResourceLocation)
     */
    @ApiStatus.Internal
    public ParticleEmitterInfo(ResourceLocation effek) {
        this(effek, null);
    }

    /**
     * @see #create(Level, ResourceLocation, ResourceLocation)
     */
    @ApiStatus.Internal
    public ParticleEmitterInfo(ResourceLocation effek, ResourceLocation emitter) {
        this.effek = effek;
        this.emitter = emitter;
        if (emitter != null) {
            flags |= FlagMask.HAS_EMITTER;
        }
    }

    @Override
    public ParticleEmitterInfo clone() {
        try {
            return (ParticleEmitterInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public final boolean hasEmitter() {
        return (flags & FlagMask.HAS_EMITTER) != 0;
    }

    public final boolean isPositionSet() {
        return (flags & FlagMask.IS_POSITION_SET) != 0;
    }

    public final boolean isRotationSet() {
        return (flags & FlagMask.IS_ROTATION_SET) != 0;
    }

    public final boolean isScaleSet() {
        return (flags & FlagMask.IS_SCALE_SET) != 0;
    }

    /**
     * @since 2.0.0
     */
    public final boolean isSpeedSet() {
        return (flags & FlagMask.IS_SPEED_SET) != 0;
    }

    public final boolean hasParameters() {
        return (flags & FlagMask.HAS_PARAMETERS) != 0;
    }

    public final boolean hasTriggers() {
        return (flags & FlagMask.HAS_TRIGGERS) != 0;
    }

    public final boolean hasBoundEntity() {
        return (flags & FlagMask.HAS_BOUND_ENTITY) != 0;
    }

    /**
     * Set whether position and rotation are in entity space.
     *
     * @return True if coordinates are in entity space, otherwise in world space.
     */
    public final boolean isEntitySpaceRelativePosSet() {
        return (flags & FlagMask.IS_ENTITY_SPACE_RELATIVE_POSITION_SET) != 0;
    }

    public final boolean usingEntityHeadSpace() {
        return (flags & FlagMask.USE_ENTITY_HEAD_SPACE) != 0;
    }

    public final boolean usingEntityVelocityAsRotation() {
        return (flags & FlagMask.USE_ENTITY_VELOCITY_AS_ROTATION) != 0;
    }

    /**
     * Set position. <br>
     * Will be relative position (in world space) if {@link #hasBoundEntity()}
     *
     * @param pos Relative/Absolute position
     * @return self
     * @see #entitySpaceRelativePosition(double, double, double) for relative position in entity space.
     */
    public ParticleEmitterInfo position(Vec3 pos) {
        return position(pos.x, pos.y, pos.z);
    }

    /**
     * Set position. <br>
     * Will be relative position if {@link #hasBoundEntity()}
     *
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @return self
     */
    public ParticleEmitterInfo position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        flags |= FlagMask.IS_POSITION_SET;
        return this;
    }

    /**
     * Set rotation in radians.<br>
     * Euler order is YXZ
     *
     * @param rot rotation vector(x, y), in radians
     * @return self
     */
    public ParticleEmitterInfo rotation(Vec2 rot) {
        return rotation(rot.x, rot.y, 0);
    }

    /**
     * Set rotation from a forward vector.
     * This method considers +Z in Effekseer editor as forward.
     *
     * @param forward the forward vector, does not need to be normalized.
     * @return self
     * @since 2.1.0
     */
    @SuppressWarnings("unused")
    public ParticleEmitterInfo rotationFromForward(Vec3 forward) {
        return rotationFromForward(forward, 0);
    }

    /**
     * Set rotation from a forward vector.
     * This method considers +Z in Effekseer editor as forward.
     *
     * @param forward the forward vector, does not need to be normalized.
     * @param rotZ Z axis rotation.
     * @return self
     * @since 2.1.0
     */
    public ParticleEmitterInfo rotationFromForward(Vec3 forward, float rotZ) {
        Vec2 rot = forward2rot(forward);
        return rotation(wrapRadians(-Mth.PI / 2 - rot.x), wrapRadians(rot.y + Mth.PI), rotZ);
    }

    /**
     * Set rotation in radians.<br>
     * Euler order is YXZ.
     *
     * @param x X rotation, in radians
     * @param y Y rotation, in radians
     * @param z Z rotation, in radians
     * @return self
     */
    public ParticleEmitterInfo rotation(float x, float y, float z) {
        this.rotX = x;
        this.rotY = y;
        this.rotZ = z;
        flags |= FlagMask.IS_ROTATION_SET;
        return this;
    }

    /**
     * Set scale of this emitter.<br>
     *
     * @param scale scale value.
     * @return self
     */
    public ParticleEmitterInfo scale(float scale) {
        return scale(scale, scale, scale);
    }

    /**
     * Set scale of this emitter.<br>
     *
     * @param x X-axis scale value.
     * @param y y-axis scale value.
     * @param z Z-axis scale value.
     * @return self
     */
    public ParticleEmitterInfo scale(float x, float y, float z) {
        this.scaleX = x;
        this.scaleY = y;
        this.scaleZ = z;
        flags |= FlagMask.IS_SCALE_SET;
        return this;
    }

    /**
     * Set relative play speed of this emitter.
     * <p>
     * WARNING: Effekseer effects are baked as 60 frames (by default), and
     * changing this value to lower than {@code 1} may look lagged / stepped.
     * <p>
     * To fix this problem, you should set the play speed in your Effekseer editor
     * as the slowest desired play speed, and call this method with the argument value not lesser than {@code 1},
     * up to your max desired relative play speed.
     * <p>
     * WARNING: Do not set speed on long-time emitters.
     * Emitters with relative speed other than {@code 1} (default)
     * will increase performance cost by time, until it has been stopped.
     *
     * @param speed relative speed.
     * @since 2.0.0
     */
    public ParticleEmitterInfo speed(float speed) {
        this.speed = speed;
        flags |= FlagMask.IS_SPEED_SET;
        return this;
    }

    public ParticleEmitterInfo parameter(int index, float value) {
        parameters.add(new DynamicParameter(index, value));
        flags |= FlagMask.HAS_PARAMETERS;
        return this;
    }

    public ParticleEmitterInfo trigger(int index) {
        triggers.add(index);
        flags |= FlagMask.HAS_TRIGGERS;
        return this;
    }

    public ParticleEmitterInfo bindOnEntity(Entity entity) {
        this.boundEntity = entity.getId();
        flags |= FlagMask.HAS_BOUND_ENTITY;
        return this;
    }

    /**
     * Set relative position in entity space. <br>
     * Will be relative position (in world space) if {@link #hasBoundEntity()}
     * @param pos Relative position in entity space
     * @return self
     */
    public ParticleEmitterInfo entitySpaceRelativePosition(Vec3 pos) {
        return entitySpaceRelativePosition(pos.x, pos.y, pos.z);
    }

    /**
     * Set relative position in entity space. <br>
     * Will be relative position (in world space) if {@link #hasBoundEntity()}
     * @param x Relative X position in entity space
     * @param y Relative Y position in entity space
     * @param z Relative Z position in entity space
     * @return self
     */
    public ParticleEmitterInfo entitySpaceRelativePosition(double x, double y, double z) {
        this.esX = x;
        this.esY = y;
        this.esZ = z;
        flags |= FlagMask.IS_ENTITY_SPACE_RELATIVE_POSITION_SET;
        return this;
    }

    public ParticleEmitterInfo useEntityHeadSpace() {
        return useEntityHeadSpace(true);
    }

    public ParticleEmitterInfo useEntityHeadSpace(boolean value) {
        if (value) {
            flags |= FlagMask.USE_ENTITY_HEAD_SPACE;
        } else {
            flags &= ~FlagMask.USE_ENTITY_HEAD_SPACE;
        }
        return this;
    }

    /**
     * Set whether to use bound entity's velocity direction as the emitter's rotation.
     *
     * @return self
     * @since 2.1.0
     */
    @SuppressWarnings("unused")
    public ParticleEmitterInfo useEntityVelocityAsRotation() {
        return useEntityVelocityAsRotation(true);
    }

    /**
     * Set whether to use bound entity's velocity direction as the emitter's rotation.
     * Useful for effeks bound to projectiles.
     *
     * @param value whether this flag is set to true
     * @return self
     * @since 2.1.0
     */
    public ParticleEmitterInfo useEntityVelocityAsRotation(boolean value) {
        if (value) {
            flags |= FlagMask.USE_ENTITY_VELOCITY_AS_ROTATION;
        } else {
            flags &= ~FlagMask.USE_ENTITY_VELOCITY_AS_ROTATION;
        }
        return this;
    }

    public final Vec3 position() {
        return isPositionSet() ? new Vec3(x, y, z) : Vec3.ZERO;
    }
    public final Vec3 rotation() {
        return isRotationSet() ? new Vec3(rotX, rotY, rotZ) : Vec3.ZERO;
    }
    public final Vec3 scale() {
        return isScaleSet() ? new Vec3(scaleX, scaleY, scaleZ) : VEC3_ONES;
    }
    /** @since 2.0.0 */
    public final float speed() {
        return isSpeedSet() ? speed : 1;
    }
    public Optional<Entity> getBoundEntity(Level level) {
        return hasBoundEntity() ? Optional.ofNullable(level.getEntity(boundEntity)) : Optional.empty();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(effek);
        buf.writeVarInt(flags);
        if (hasEmitter()) {
            buf.writeResourceLocation(emitter);
        }
        if (isPositionSet()) {
            buf.writeDouble(x);
            buf.writeDouble(y);
            buf.writeDouble(z);
        }
        if (isRotationSet()) {
            buf.writeFloat(rotX);
            buf.writeFloat(rotY);
            buf.writeFloat(rotZ);
        }
        if (isScaleSet()) {
            buf.writeFloat(scaleX);
            buf.writeFloat(scaleY);
            buf.writeFloat(scaleZ);
        }
        if (isSpeedSet()) {
            buf.writeFloat(speed);
        }
        if (hasParameters()) {
            buf.writeVarInt(parameters.size());
            parameters.forEach(param -> {
                buf.writeVarInt(param.index());
                buf.writeFloat(param.value());
            });
        }
        if (hasTriggers()) {
            buf.writeVarIntArray(triggers.toIntArray());
        }
        if (hasBoundEntity()) {
            buf.writeVarInt(boundEntity);
        }
        if (isEntitySpaceRelativePosSet()) {
            buf.writeDouble(esX);
            buf.writeDouble(esY);
            buf.writeDouble(esZ);
        }
    }

    public ParticleEmitterInfo(FriendlyByteBuf buf) {
        effek = buf.readResourceLocation();
        flags = buf.readVarInt();
        if (hasEmitter()) {
            emitter = buf.readResourceLocation();
        } else {
            emitter = null;
        }
        if (isPositionSet()) {
            x = buf.readDouble();
            y = buf.readDouble();
            z = buf.readDouble();
        }
        if (isRotationSet()) {
            rotX = buf.readFloat();
            rotY = buf.readFloat();
            rotZ = buf.readFloat();
        }
        if (isScaleSet()) {
            scaleX = buf.readFloat();
            scaleY = buf.readFloat();
            scaleZ = buf.readFloat();
        }
        if (isSpeedSet()) {
            speed = buf.readFloat();
        }
        if (hasParameters()) {
            var paramCount = buf.readVarInt();
            for (int i = 0; i < paramCount; i++) {
                var index = buf.readVarInt();
                var value = buf.readFloat();
                parameters.add(new DynamicParameter(index, value));
            }
        }
        if (hasTriggers()) {
            triggers.addElements(0, buf.readVarIntArray());
        }
        if (hasBoundEntity()) {
            boundEntity = buf.readVarInt();
        }
        if (isEntitySpaceRelativePosSet()) {
            esX = buf.readDouble();
            esY = buf.readDouble();
            esZ = buf.readDouble();
        }
    }

    @ApiStatus.Internal
    public void spawnInWorld(Level level, Player player) {
        if (NativePlatform.isRunningOnUnsupportedPlatform()) {
            return;
        }
        var loaded = Optional.ofNullable(EffectRegistry.get(effek)).map(EffectHolder::load);
        loaded.ifPresent(future -> future.thenAccept(def -> def.ifPresent(effek -> {
            var emitter = hasEmitter() ? effek.play(this.emitter) : effek.play();
            var hasBoundEntity = hasBoundEntity();
            var isPositionSet = isPositionSet();
            var isRotationSet = isRotationSet();
            var isScaleSet = isScaleSet();
            var isSpeedSet = isSpeedSet();
            var hasParams = hasParameters();
            var hasTriggs = hasTriggers();
            float x, y, z;
            if (isPositionSet) {
                x = (float) this.x;
                y = (float) this.y;
                z = (float) this.z;
            } else if (!hasBoundEntity && player != null) {
                x = (float) player.getX();
                y = (float) player.getY();
                z = (float) player.getZ();
            } else {
                x = y = z = 0;
            }
            if (!hasBoundEntity) {
                emitter.setPosition(x, y, z);
            }

            if (isRotationSet) {
                emitter.setRotation(rotX, rotY, rotZ);
            }
            if (isScaleSet) {
                emitter.setScale(scaleX, scaleY, scaleZ);
            }
            if (isSpeedSet) {
                emitter.setSpeed(speed);
            }

            if (hasParams) {
                for (var parameter : parameters) {
                    emitter.setDynamicInput(parameter.index(), parameter.value());
                }
            }
            if (hasTriggs) {
                triggers.forEach(emitter::sendTrigger);
            }

            if (hasBoundEntity) {
                var entity = new WeakReference<>(level.getEntity(boundEntity));
                var headSpace = usingEntityHeadSpace();
                var entitySpace = headSpace || isEntitySpaceRelativePosSet();
                var velocitySpace = usingEntityVelocityAsRotation();
                var rotZ = this.rotZ;
                ParticleEmitter.PreDrawCallback updater = (em, partial) -> {
                    Optional.ofNullable(entity.get()).filter(Entity::isAlive).ifPresentOrElse(et -> {
                        float relX, relY, relZ;
                        if (entitySpace) {
                            Basis basis;
                            float rotY;
                            float rotX;
                            if (headSpace) {
                                rotY = (float) Math.toRadians(et.getViewYRot(partial));
                                rotX = (float) Math.toRadians(et.getViewXRot(partial));
                                basis = Basis.fromEuler(new Vec3(-rotX, Mth.PI - rotY, rotZ));
                            } else {
                                rotY = (float) Math.toRadians(Mth.lerp(partial, et.yRotO, et.getYRot()));
                                rotX = 0;
                                basis = Basis.fromEntityBody(et);
                            }
                            if (velocitySpace) {
                                var rot = forward2rot(et.getDeltaMovement());
                                rotY = -rot.y;
                                rotX = rot.x - (float) (Math.PI / 2);
                            }
                            var esRelPos = basis.toGlobal(new Vec3(esX, esY, esZ));
                            relX = (float) (x + esRelPos.x);
                            relY = (float) (y + esRelPos.y);
                            relZ = (float) (z + esRelPos.z);
                            em.setRotation(this.rotX + rotX, this.rotY - rotY, rotZ);
                        } else {
                            relX = x;
                            relY = y;
                            relZ = z;
                        }
                        em.setPosition(
                                (float) Mth.lerp(partial, et.xOld, et.getX()) + relX,
                                (float) Mth.lerp(partial, et.yOld, et.getY()) + relY + (headSpace ? et.getEyeHeight() : 0),
                                (float) Mth.lerp(partial, et.zOld, et.getZ()) + relZ
                        );
                    }, em::stop);
                };
                updater.accept(emitter, 0);
                emitter.addPreDrawCallback(updater);
            }
        })));
    }

    @ApiStatus.Internal
    public void copyTo(ParticleEmitterInfo target) {
        target.flags = this.flags;
        target.x = this.x;
        target.y = this.y;
        target.z = this.z;
        target.rotX = this.rotX;
        target.rotY = this.rotY;
        target.rotZ = this.rotZ;
        target.scaleX = this.scaleX;
        target.scaleY = this.scaleY;
        target.scaleZ = this.scaleZ;
        target.speed = this.speed;
        target.parameters.clear();
        target.parameters.addAll(this.parameters);
        target.triggers.clear();
        target.triggers.addAll(this.triggers);
        target.boundEntity = this.boundEntity;
    }

    private static Vec2 forward2rot(Vec3 forward) {
        double dx = forward.x();
        double dy = forward.y();
        double dz = forward.z();
        double xz = Math.sqrt(dx * dx + dz * dz);
        double rx = -wrapRadians(Mth.atan2(dy, xz) - Math.PI / 2);
        double ry = -wrapRadians(Mth.atan2(dz, dx) - Math.PI / 2);
        return new Vec2((float) rx, (float) ry);
    }

    private static float wrapRadians(float radians) {
        return (float) wrapRadians((double) radians);
    }

    private static double wrapRadians(double radians) {
        return Math.toRadians(Mth.wrapDegrees(Math.toDegrees(radians)));
    }
}
