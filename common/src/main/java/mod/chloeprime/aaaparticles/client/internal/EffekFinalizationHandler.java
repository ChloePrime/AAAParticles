package mod.chloeprime.aaaparticles.client.internal;

import mod.chloeprime.aaaparticles.api.client.EffectDefinition;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.client.metadata.EffectFinalization;
import mod.chloeprime.aaaparticles.client.ClientPlatformMethods;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;
import java.util.PriorityQueue;

public class EffekFinalizationHandler {
    public record Entry(
            ParticleEmitter emitter,
            long destroyTime
    ) {
    }

    public static void finalize(EffectDefinition effek, ParticleEmitter emitter) {
        var settings = (EffectFinalization) effek.getMetadata().getFinalizationSettings().orElse(null);
        if (settings == null) {
            emitter.stop();
            return;
        }
        var level = MC.level;
        if (level == null) {
            emitter.stop();
            return;
        }

        emitter.sendTrigger(settings.trigger());

        var now = level.getGameTime();
        DESTROY_EVENTS.add(new Entry(emitter, now + settings.delay()));
    }

    private static final Minecraft MC = Minecraft.getInstance();
    private static final PriorityQueue<Entry> DESTROY_EVENTS = new PriorityQueue<>(Comparator.comparingLong(Entry::destroyTime));

    @ApiStatus.Internal
    public static void setup() {
        ClientPlatformMethods.get().addClientPostTickCallback(EffekFinalizationHandler::tick);
    }

    private static void tick(Minecraft client) {
        var level = client.level;
        if (level == null) {
            clear();
            return;
        }
        var now = level.getGameTime();
        while (true) {
            var head = DESTROY_EVENTS.peek();
            if (head == null || head.destroyTime > now) {
                break;
            }
            DESTROY_EVENTS.remove().emitter().stop();
        }
    }

    private static void clear() {
        DESTROY_EVENTS.forEach(event -> event.emitter().stop());
        DESTROY_EVENTS.clear();
    }
}
