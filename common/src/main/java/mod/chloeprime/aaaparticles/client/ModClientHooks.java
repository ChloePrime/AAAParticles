package mod.chloeprime.aaaparticles.client;

import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.world.entity.LightningBolt;

public class ModClientHooks {
    public static final ParticleEmitterInfo LIGHTNING_EFFEK_TEMPLATE = new ParticleEmitterInfo(AAAParticles.loc("lightning"));

    public static void playLightningEffek(LightningBolt bolt) {
        var info = LIGHTNING_EFFEK_TEMPLATE.clone().position(bolt.position());
        AAALevel.addParticle(bolt.level, true, info);
    }
}
