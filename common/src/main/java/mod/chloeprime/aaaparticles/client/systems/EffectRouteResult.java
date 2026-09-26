package mod.chloeprime.aaaparticles.client.systems;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

/**
 * @since 2.3.0
 */
public record EffectRouteResult(
        @Nullable Identifier id,
        @Nullable Int2DoubleMap params,
        @Nullable int[] triggers
) {
}
