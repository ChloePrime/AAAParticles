package mod.chloeprime.aaaparticles.client;

import dev.architectury.registry.ReloadListenerRegistry;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.DynamicParameter;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import mod.chloeprime.aaaparticles.client.installer.JarExtractor;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import mod.chloeprime.aaaparticles.client.loader.EffekAssetLoader;
import mod.chloeprime.aaaparticles.client.registry.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class AAAParticlesClient
{

	public static void init() {
		installNativeLibrary();
		if (!NativePlatform.isDataGen()) {
			ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new EffekAssetLoader(), AAAParticles.loc("effek"));
		}
		Debug.INSTANCE.registerDebugHooks();
	}

	public static void setup() {
	}

	private static void installNativeLibrary() {
		var platform = NativePlatform.current();
		if (platform.isUnsupported()) {
			return;
		}
		var DLL_NAME = "EffekseerNativeForJava";
		var dll = platform.getNativeInstallPath(DLL_NAME);
		try {
			var resource = "assets/%s/%s".formatted(AAAParticles.MOD_ID, platform.formatFileName(DLL_NAME));
			if (!dll.isFile()) {
                AAAParticles.LOGGER.info("Installing Effekseer native library at {}", dll.getCanonicalPath());
				JarExtractor.extract(resource, dll);
			} else {
				var updated = JarExtractor.update(resource, dll);
				if (updated) {
                    AAAParticles.LOGGER.info("Updating Effekseer native library at {}", dll.getCanonicalPath());
				} else {
                    AAAParticles.LOGGER.info("Loading Effekseer native library at {}", dll.getCanonicalPath());
				}
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

	public static void setParam(ParticleEmitter.Type type, ResourceLocation effek, ResourceLocation emitterName, DynamicParameter[] params) {
		Optional.ofNullable(EffectRegistry.get(effek))
				.flatMap(mng -> mng.getNamedEmitter(type, emitterName))
				.ifPresent(emitter -> {
					for (var param : params) {
						emitter.setDynamicInput(param.index(), param.value());
					}
				});
	}

	public static void sendTrigger(ParticleEmitter.Type type, ResourceLocation effek, ResourceLocation emitterName, int[] triggers) {
		Optional.ofNullable(EffectRegistry.get(effek))
				.flatMap(mng -> mng.getNamedEmitter(type, emitterName))
				.ifPresent(emitter -> Arrays.stream(triggers).forEach(emitter::sendTrigger));
	}
}
