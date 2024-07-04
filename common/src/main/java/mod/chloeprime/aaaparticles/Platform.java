package mod.chloeprime.aaaparticles;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import mod.chloeprime.aaaparticles.common.network.S2CAddParticle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public final class Platform {
    private Platform() {
    }

    private static int simpleLoaderCache = -1;

    public static boolean isFabric() {
        updateLoaderCache();
        return simpleLoaderCache == 0;
    }

    public static boolean isForge() {
        updateLoaderCache();
        return simpleLoaderCache == 1;
    }

    private static void updateLoaderCache() {
        if (simpleLoaderCache != -1) {
            return;
        }

        switch (ArchitecturyTarget.getCurrentTarget()) {
            case "fabric" -> simpleLoaderCache = 0;
            case "forge" -> simpleLoaderCache = 1;
        }
    }

    /**
     * Checks whether a mod with the given mod ID is present.
     *
     * @param id The mod ID to check.
     * @return <code>true</code> if the mod is loaded, <code>false</code> otherwise.
     */
    @ExpectPlatform
    public static boolean isModLoaded(String id) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendEffectPacket(ServerPlayer player, S2CAddParticle packet)
    {
        throw new AssertionError();
    }

    public static void register(PackType type, PreparableReloadListener listener) {
        register(type, listener, null);
    }

    public static void register(PackType type, PreparableReloadListener listener, @Nullable ResourceLocation listenerId) {
        register(type, listener, listenerId, List.of());
    }

    @ExpectPlatform
    public static void register(PackType type, PreparableReloadListener listener, @Nullable ResourceLocation listenerId, Collection<ResourceLocation> dependencies) {
        throw new AssertionError();
    }
}