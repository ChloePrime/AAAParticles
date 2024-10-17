package mod.chloeprime.aaaparticles.api.common;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import mod.chloeprime.aaaparticles.client.registry.EffectRegistry;
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
            flags |= 1;
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
        return (flags & 1) != 0;
    }

    public final boolean isPositionSet() {
        return (flags & 2) != 0;
    }

    public final boolean isRotationSet() {
        return (flags & 4) != 0;
    }

    public final boolean isScaleSet() {
        return (flags & 8) != 0;
    }

    public final boolean hasParameters() {
        return (flags & 128) != 0;
    }

    public final boolean hasTriggers() {
        return (flags & 256) != 0;
    }

    public final boolean hasBoundEntity() {
        return (flags & 16) != 0;
    }

    /**
     * Set whether position and rotation are in entity space.
     * @return True if coordinates are in entity space, otherwise in world space.
     */
    public final boolean isEntitySpaceRelativePosSet() {
        return (flags & 32) != 0;
    }

    public final boolean usingEntityHeadSpace() {
        return (flags & 64) != 0;
    }

    /**
     * Set position. <br>
     * Will be relative position (in world space) if {@link #hasBoundEntity()}
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
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @return self
     */
    public ParticleEmitterInfo position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        flags |= 2;
        return this;
    }

    /**
     * Set rotation in radians.<br>
     * Euler order is YXZ
     * @param rot rotation vector(x, y), in radians
     * @return self
     */
    public ParticleEmitterInfo rotation(Vec2 rot) {
        return rotation(rot.x, rot.y, 0);
    }

    /**
     * Set rotation in radians.<br>
     * Euler order is YXZ
     * @param x X rotation, in radians
     * @param y Y rotation, in radians
     * @param z Z rotation, in radians
     * @return self
     */
    public ParticleEmitterInfo rotation(float x, float y, float z) {
        this.rotX = x;
        this.rotY = y;
        this.rotZ = z;
        flags |= 4;
        return this;
    }

    public ParticleEmitterInfo scale(float scale) {
        return scale(scale, scale, scale);
    }

    public ParticleEmitterInfo scale(float x, float y, float z) {
        this.scaleX = x;
        this.scaleY = y;
        this.scaleZ = z;
        flags |= 8;
        return this;
    }

    public ParticleEmitterInfo parameter(int index, float value) {
        parameters.add(new DynamicParameter(index, value));
        flags |= 128;
        return this;
    }

    public ParticleEmitterInfo trigger(int index) {
        triggers.add(index);
        flags |= 256;
        return this;
    }

    public ParticleEmitterInfo bindOnEntity(Entity entity) {
        this.boundEntity = entity.getId();
        flags |= 16;
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
        flags |= 32;
        return this;
    }

    public ParticleEmitterInfo useEntityHeadSpace() {
        return useEntityHeadSpace(true);
    }

    public ParticleEmitterInfo useEntityHeadSpace(boolean value) {
        if (value) {
            flags |= 64;
        } else {
            flags &= ~64;
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
        Optional.ofNullable(EffectRegistry.get(effek)).ifPresent(effek -> {
            var emitter = hasEmitter() ? effek.play(this.emitter) : effek.play();
            var hasBoundEntity = hasBoundEntity();
            var isPositionSet = isPositionSet();
            var isRotationSet = isRotationSet();
            var isScaleSet = isScaleSet();
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
            emitter.setPosition(x, y, z);

            if (isRotationSet) {
                emitter.setRotation(rotX, rotY, rotZ);
            }
            if (isScaleSet) {
                emitter.setScale(scaleX, scaleY, scaleZ);
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
                var rotZ = this.rotZ;
                emitter.addPreDrawCallback((em, partial) -> {
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
                });
            }
        });
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
        target.parameters.clear();
        target.parameters.addAll(this.parameters);
        target.triggers.clear();
        target.triggers.addAll(this.triggers);
        target.boundEntity = this.boundEntity;
    }
}
