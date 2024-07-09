package mod.chloeprime.aaaparticles.client;

import com.zigythebird.multiloaderutils.utils.NetworkManager;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import mod.chloeprime.aaaparticles.client.installer.JarExtractor;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import mod.chloeprime.aaaparticles.common.network.S2CAddParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.io.IOException;

public class AAAParticlesClient
{
	public static final ResourceLocation PACKET_ID = AAAParticles.loc("packet");

	public static void init() {
		installNativeLibrary();
		Debug.INSTANCE.registerDebugHooks();

		NetworkManager.registerReceiver(NetworkManager.Side.S2C, PACKET_ID, (FriendlyByteBuf buf, NetworkManager.PacketContext context) -> {
			S2CAddParticle packet = new S2CAddParticle(buf);
			packet.spawnInWorld(Minecraft.getInstance().level, Minecraft.getInstance().player);
		});
	}

	public static void setup() {
	}

	private static void installNativeLibrary() {
		var platform = NativePlatform.current();
		var DLL_NAME = "EffekseerNativeForJava";
		var dll = platform.getNativeInstallPath(DLL_NAME);
		try {
			if (!dll.isFile()) {
				AAAParticles.LOGGER.info("Installing Effekseer native library at " + dll.getCanonicalPath());
				var resource = "assets/%s/%s".formatted(AAAParticles.MOD_ID, platform.formatFileName(DLL_NAME));
				JarExtractor.extract(resource, dll);
			} else {
				AAAParticles.LOGGER.info("Loading Effekseer native library at " + dll.getCanonicalPath());
			}
			System.load(dll.getCanonicalPath());
		} catch (IOException | UnsatisfiedLinkError e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static void addParticle(Level level, ParticleEmitterInfo info) {
		var player = Minecraft.getInstance().player;
		if (player != null && player.level() != level) {
			return;
		}
		info.spawnInWorld(level, player);
	}
}
