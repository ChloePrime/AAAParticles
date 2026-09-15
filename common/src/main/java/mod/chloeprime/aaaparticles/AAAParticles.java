package mod.chloeprime.aaaparticles;

import com.mojang.logging.LogUtils;
import mod.chloeprime.aaaparticles.common.network.ModNetwork;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class AAAParticles {
	public static final String MOD_ID = "aaa_particles";
	public static final String LOG_PREFIX = "[AAAParticles]";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static void init() {
		ModNetwork.init();
	}

	/**
	 * @since 2.3.0
	 */
	public static ResourceLocation loc(String namespace, String path) {
		return new ResourceLocation(namespace, path);
	}

	public static ResourceLocation loc(String path) {
		return loc(MOD_ID, path);
	}
}
