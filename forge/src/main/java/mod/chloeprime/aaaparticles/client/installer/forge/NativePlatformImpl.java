package mod.chloeprime.aaaparticles.client.installer.forge;

import net.minecraftforge.fml.ModLoader;

@SuppressWarnings("unused")
public class NativePlatformImpl {
    public static boolean isDataGen() {
        return ModLoader.isDataGenRunning() ;
    }
}
