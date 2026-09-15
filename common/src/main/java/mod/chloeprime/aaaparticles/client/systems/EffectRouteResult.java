package mod.chloeprime.aaaparticles.client.systems;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * @since 2.3.0
 */
public record EffectRouteResult(
        @Nullable ResourceLocation id,
        @Nullable Int2DoubleMap params,
        @Nullable int[] triggers
) {
}
