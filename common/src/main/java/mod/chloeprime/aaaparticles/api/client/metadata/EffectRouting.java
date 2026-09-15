package mod.chloeprime.aaaparticles.api.client.metadata;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleMaps;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import mod.chloeprime.aaaparticles.AAAParticles;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Effect routing configuration per level
 *
 * @param configuredTargetId configured target id. Use {@link #targetId()} to get the real target id.
 * @param params extra dynamic inputs when routed
 * @param triggers extra triggers when routed
 * @since 2.3
 */
public record EffectRouting(
        ResourceLocation configuredTargetId,
        Int2DoubleMap params,
        int[] triggers
) {
    /**
     * An internal mark to replace {@code null}
     */
    @ApiStatus.Internal
    private static final ResourceLocation NULL_MARK = AAAParticles.loc("$$NULL_MARK$$");

    /**
     * Empty param list
     */
    public static final Int2DoubleMap EMPTY_PARAMS = Int2DoubleMaps.EMPTY_MAP;

    /**
     * Empty trigger list
     */
    public static final int[] EMPTY_TRIGGERS = new int[0];

    public static final Codec<EffectRouting> DIRECT_ENTRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC
                    .optionalFieldOf("target", NULL_MARK)
                    .forGetter(EffectRouting::configuredTargetId),
            Codec.unboundedMap(Codec.INT, Codec.DOUBLE)
                    .<Int2DoubleMap>xmap(Int2DoubleOpenHashMap::new, m -> m)
                    .optionalFieldOf("params", EMPTY_PARAMS)
                    .forGetter(EffectRouting::params),
            Codec.INT_STREAM
                    .xmap(IntStream::toArray, Arrays::stream)
                    .optionalFieldOf("triggers", EMPTY_TRIGGERS)
                    .forGetter(EffectRouting::triggers)
    ).apply(instance, EffectRouting::new));

    public static final Codec<EffectRouting> ENTRY_CODEC = Codec
            .either(ResourceLocation.CODEC, DIRECT_ENTRY_CODEC)
            .xmap(either -> either.map(id -> new EffectRouting(id, EMPTY_PARAMS, EMPTY_TRIGGERS), Function.identity()), Either::right);

    public static final Codec<Map<QualityOptions, EffectRouting>> TABLE_CODEC = Codec.unboundedMap(QualityOptions.CODEC, ENTRY_CODEC);
    public static final Map<QualityOptions, EffectRouting> EMPTY_TABLE = Map.of();

    /**
     * 获取路由目标的 id。
     * 如果配置里没填则返回 {@link Optional#empty()}
     *
     * @return 路由目标的 id
     */
    public Optional<ResourceLocation> targetId() {
        return NULL_MARK.equals(configuredTargetId) ? Optional.empty() : Optional.ofNullable(configuredTargetId);
    }

    public enum QualityOptions implements StringRepresentable {
        LOWEST,
        LOW,
        MEDIUM,
        HIGH,
        HIGHEST;

        private static final QualityOptions[] VALUES = values();
        private static final Codec<QualityOptions> CODEC = StringRepresentable.fromEnum(() -> VALUES);
        private final String name = name().toLowerCase(Locale.ROOT);

        @Override
        public @Nonnull String getSerializedName() {
            return name;
        }

        public Optional<QualityOptions> next() {
            return ordinal() >= VALUES.length - 1 ? Optional.empty() : Optional.of(VALUES[ordinal() + 1]);
        }

        @SuppressWarnings("unused")
        public Optional<QualityOptions> prev() {
            return ordinal() == 0 ? Optional.empty() : Optional.of(VALUES[ordinal() - 1]);
        }

        public static QualityOptions current() {
            return inferenceFromCurrentsGraphicsSettings();
        }

        public static QualityOptions inferenceFromCurrentsGraphicsSettings() {
            var options = Minecraft.getInstance().options;
            int particleScore = switch (options.particles().get()) {
                case ALL -> 3;
                case MINIMAL -> 0;
                default -> 2;
            };
            int qualityScore = options.graphicsMode().get() == GraphicsStatus.FAST ? 0 : 1;
            int index = Mth.clamp(particleScore + qualityScore, 0, VALUES.length - 1);
            return VALUES[index];
        }
    }
}
