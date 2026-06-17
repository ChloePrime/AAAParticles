package mod.chloeprime.aaaparticles.forge.compat.kubejs;

import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;

public class AAAParticleKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void registerClasses(ClassFilter filter) {
        filter.allow("mod.chloeprime.aaaparticles.api");
    }

    @Override
    public void registerBindings(BindingRegistry event) {
        event.add("AAAParticles", AAALevel.class);
        event.add(ParticleEmitterInfo.class.getSimpleName(), ParticleEmitterInfo.class);
    }
}
