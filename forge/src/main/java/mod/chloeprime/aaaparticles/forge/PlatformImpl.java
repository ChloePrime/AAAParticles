package mod.chloeprime.aaaparticles.forge;

import com.google.common.collect.Lists;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.common.network.S2CAddParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber(modid = AAAParticles.MOD_ID)
public class PlatformImpl
{
    public static boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    public static void sendEffectPacket(ServerPlayer player, S2CAddParticle packet)
    {
        AAAParticlesForge.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
    }

    private static List<PreparableReloadListener> serverDataReloadListeners = Lists.newArrayList();

    public static void register(PackType type, PreparableReloadListener listener, @Nullable ResourceLocation listenerId, Collection<ResourceLocation> dependencies) {
        if (type == PackType.SERVER_DATA) {
            serverDataReloadListeners.add(listener);
        } else if (type == PackType.CLIENT_RESOURCES) {
            registerClient(listener);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void registerClient(PreparableReloadListener listener) {
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(listener);
    }

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        for (PreparableReloadListener listener : serverDataReloadListeners) {
            event.addListener(listener);
        }
    }
}
