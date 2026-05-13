package mod.chloeprime.aaaparticles.common.util;

import com.google.common.base.Suppliers;
import mod.chloeprime.aaaparticles.AAAParticles;
import mod.chloeprime.aaaparticles.client.installer.NativePlatform;
import net.minecraft.util.Mth;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class Helpers {
    public static <T> T checkPlatform(Supplier<T> constructor) {
        if (NativePlatform.isRunningOnUnsupportedPlatform()) {
            throw new UnsupportedOperationException("Unsupported platform");
        }
        return constructor.get();
    }

    public static int getSuggestedInstanceCount() {
        int loCount = 1_0000;
        int hiCount = 100_0000;
        int loMem = 8;
        int hiMem = 128;
        var systemMemory = getSystemMemory();
        if (systemMemory < 0) {
            return -1;
        }
        var systemMemoryGB = systemMemory / 1024.0 / 1024 / 1024;
        if (systemMemoryGB <= loMem) {
            return loCount;
        }
        if (systemMemoryGB >= hiMem) {
            return hiCount;
        }
        var alpha = (systemMemoryGB - loMem) / (hiMem - loMem);
        return (int) Mth.lerp(alpha, loCount, hiCount);
    }

    public static long getSystemMemory() {
        return PHYSICAL_MEMORY_KB_FROM_M_BEAN.getAsLong();
    }

    private static final LongSupplier PHYSICAL_MEMORY_KB_FROM_M_BEAN = Suppliers.memoize(() -> {
        var mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            Object attribute = mBeanServer.getAttribute(new ObjectName("java.lang","type","OperatingSystem"), "TotalPhysicalMemorySize");
            return Long.parseLong(attribute.toString());
        } catch (Exception ex) {
            AAAParticles.LOGGER.error("Failed to get physical memory from mBean", ex);
            return -1L;
        }
    })::get;
}
