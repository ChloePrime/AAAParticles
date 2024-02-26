package mod.chloeprime.aaaparticles.common.network;

import dev.architectury.networking.NetworkChannel;
import mod.chloeprime.aaaparticles.AAAParticles;

public class ModNetwork {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(AAAParticles.loc("main"));

    public static void init() {
        CHANNEL.register(S2CAddParticle.class, S2CAddParticle::encode, S2CAddParticle::new, S2CAddParticle::handle);
    }
}
