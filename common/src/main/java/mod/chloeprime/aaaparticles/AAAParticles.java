package mod.chloeprime.aaaparticles;

import com.mojang.logging.LogUtils;
import mod.chloeprime.aaaparticles.common.network.ModNetwork;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class AAAParticles
{
	public static final String MOD_ID = "aaa_particles";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static void init() {
		ModNetwork.init();
	}

    public static ResourceLocation loc(String path) {
		return new ResourceLocation(MOD_ID, path);
    }
}
