package mod.chloeprime.aaaparticles.fabric;

import com.google.common.primitives.Longs;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.common.network.S2CAddParticle;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PlatformImpl
{
    public static boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    public static void sendEffectPacket(ServerPlayer player, S2CAddParticle packet)
    {
        FriendlyByteBuf buf = PacketByteBufs.create();

        packet.encode(buf);

        ServerPlayNetworking.send(player, AAAParticles.PARTICLE_PACKET_ID, buf);
    }

    private static final SecureRandom RANDOM = new SecureRandom();

    public static void register(PackType type, PreparableReloadListener listener, @Nullable ResourceLocation listenerId, Collection<ResourceLocation> dependencies) {
        var bytes = new byte[8];
        RANDOM.nextBytes(bytes);
        var id = listenerId != null ? listenerId : new ResourceLocation("architectury:reload_" + StringUtils.leftPad(Math.abs(Longs.fromByteArray(bytes)) + "", 19, '0'));
        ResourceManagerHelper.get(type).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return id;
            }

            @Override
            public String getName() {
                return listener.getName();
            }

            @Override
            public Collection<ResourceLocation> getFabricDependencies() {
                return dependencies;
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
                return listener.reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2);
            }
        });
    }
}
