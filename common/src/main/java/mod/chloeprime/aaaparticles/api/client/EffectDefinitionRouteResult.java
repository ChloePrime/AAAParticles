package mod.chloeprime.aaaparticles.api.client;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Result of {@link EffectDefinition#route}
 *
 * @param definition Routed and loaded definition
 * @param params Extra dynamic inputs for this route
 * @param triggers Extra triggers for this route
 * @since 2.3
 */
public record EffectDefinitionRouteResult(
        @Nonnull  EffectDefinition definition,
        @Nullable Int2DoubleMap params,
        @Nullable int[] triggers
) {
}
