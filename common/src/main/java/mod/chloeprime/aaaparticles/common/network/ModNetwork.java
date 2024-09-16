package mod.chloeprime.aaaparticles.common.network;

import dev.architectury.networking.NetworkChannel;
import mod.chloeprime.aaaparticles.AAAParticles;

public class ModNetwork {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(AAAParticles.loc("main"));

    public static void init() {
        CHANNEL.register(S2CAddParticle.class, S2CAddParticle::encode, S2CAddParticle::new, S2CAddParticle::handle);
        CHANNEL.register(S2CUpdateEmitterParam.class, S2CUpdateEmitterParam::encode, S2CUpdateEmitterParam::new, S2CUpdateEmitterParam::handle);
        CHANNEL.register(S2CUpdateEmitterParam.class, S2CUpdateEmitterParam::encode, S2CUpdateEmitterParam::new, S2CUpdateEmitterParam::handle);
        CHANNEL.register(S2CSendEmitterTrigger.class, S2CSendEmitterTrigger::encode, S2CSendEmitterTrigger::new, S2CSendEmitterTrigger::handle);
    }
}
