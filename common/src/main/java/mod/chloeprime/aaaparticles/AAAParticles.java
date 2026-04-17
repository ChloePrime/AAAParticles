package mod.chloeprime.aaaparticles;

import com.mojang.logging.LogUtils;
import mod.chloeprime.aaaparticles.common.network.ModNetwork;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public class AAAParticles
{
	public static final String MOD_ID = "aaa_particles";
	public static final String MOD_NAME = "AAA Particles";
	public static final String LOG_PREFIX = "[AAAParticles]";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static void init() {
		ModNetwork.init();
	}

    public static Identifier loc(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
