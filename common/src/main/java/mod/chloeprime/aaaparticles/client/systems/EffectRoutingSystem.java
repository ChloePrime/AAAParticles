package mod.chloeprime.aaaparticles.client.systems;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import mod.chloeprime.aaaparticles.api.client.metadata.EffectRouting;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Effect Route System。
 *
 * @since 2.3.0
 */
public final class EffectRoutingSystem {
    /**
     * Core route algorithm.
     * Compute route based on config and current quality settings
     * <p>
     * ID and params are routed separately.
     * If there are two quality levels that one has configured ID and the other has configured params/triggers,
     * and the given quality is lower or equal to both of such configured levels,
     * the final result will use the ID and params/triggers combined from both.
     * <p>
     * Params and triggers, instead, only matches to one matched config.
     *
     * @param config route config
     * @param current current quality level
     * @return routed effek ID and params/triggers。
     */
    public static EffectRouteResult route(
            Map<EffectRouting.QualityOptions, EffectRouting> config,
            EffectRouting.QualityOptions current
    ) {
        var id = (ResourceLocation) null;
        var params = (Int2DoubleMap) null;
        var triggers = (int[]) null;
        var opt = current;
        do {
            // resolve id
            var thisConfig = config.get(opt);
            var thisId = Optional.ofNullable(thisConfig)
                    .flatMap(EffectRouting::targetId)
                    .orElse(null);
            if (thisId != null && id == null) {
                id = thisId;
            }
            // resolve parameters and triggers
            var thisParams = Optional.ofNullable(thisConfig)
                    .map(EffectRouting::params)
                    .filter(map -> !map.isEmpty())
                    .orElse(null);
            var thisTriggers = Optional.ofNullable(thisConfig)
                    .map(EffectRouting::triggers)
                    .filter(array -> array.length != 0)
                    .orElse(null);
            if ((thisTriggers != null || thisParams != null) && (params == null && triggers == null)) {
                params = Objects.requireNonNullElse(thisParams, EffectRouting.EMPTY_PARAMS);
                triggers = Objects.requireNonNullElse(thisTriggers, EffectRouting.EMPTY_TRIGGERS);
            }
            // when both are resolved,
            // break
            if (id != null && params != null && triggers != null) {
                break;
            }
        } while ((opt = opt.next().orElse(null)) != null);

        return new EffectRouteResult(id, params, triggers);
    }

    private EffectRoutingSystem() {
    }
}
