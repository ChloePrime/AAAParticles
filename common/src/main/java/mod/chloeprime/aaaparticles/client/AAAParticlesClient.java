package mod.chloeprime.aaaparticles.client;

import java.io.IOException;

import dev.architectury.registry.ReloadListenerRegistry;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import mod.chloeprime.aaaparticles.client.installer.JarExtractor;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import mod.chloeprime.aaaparticles.client.loader.EffekAssetLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.Level;

public class AAAParticlesClient
{

	public static void init() {
		installNativeLibrary();
		ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new EffekAssetLoader(), AAAParticles.loc("effek"));
		Debug.INSTANCE.registerDebugHooks();
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
		if (player != null && player.level != level) {
			return;
		}
		info.spawnInWorld(level, player);
	}
}
