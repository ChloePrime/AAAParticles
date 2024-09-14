package mod.chloeprime.aaaparticles.client.installer;

import com.google.common.base.Suppliers;
import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import net.minecraft.Util;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum NativePlatform {
    WINDOWS(".dll"),
    LINUX(".so", "lib"),
    MACOS(".dylib", "lib", true);

    public static boolean isRunningOnUnsupportedPlatform() {
        return current().unsupported || MiscPlatformHelper.get().isDataGen();
    }

    public static NativePlatform current() {
        return CURRENT.get();
    }

    public boolean isUnsupported() {
        return unsupported;
    }

    public File getNativeInstallPath(String dllName) {
        return new File(INSTALL_FOLDER.get(), formatFileName(dllName));
    }

    public String formatFileName(String dllName) {
        return prefix + dllName + libFormat;
    }

    public String getLibraryFormat() {
        return libFormat;
    }
    private static final Supplier<NativePlatform> CURRENT = Suppliers.memoize(NativePlatform::findCurrent);
    private static final Supplier<File> INSTALL_FOLDER = Suppliers.memoize(NativePlatform::findNativeFolder);
    private final String prefix;
    private final String libFormat;

    private final boolean unsupported;

    NativePlatform(String libFormat) {
        this(libFormat, "");
    }


    NativePlatform(String libFormat, String prefix) {
        this(libFormat, prefix, false);
    }

    NativePlatform(String libFormat, String prefix, boolean unsupported) {
        this.libFormat = libFormat;
        this.prefix = prefix;
        this.unsupported = unsupported;
    }

    public static NativePlatform findCurrent() {
        return switch (Util.getPlatform()) {
            case LINUX -> LINUX;
            case SOLARIS -> throw new UnsupportedOperationException("Solaris");
            case WINDOWS -> WINDOWS;
            case OSX -> MACOS;
            case UNKNOWN -> throw new UnsupportedOperationException("Unknown Platform");
        };
    }

    private static File findNativeFolder() {
        var root = new File("./");
        return Optional.ofNullable(root.listFiles((dir, name) -> {
                    if (!dir.isDirectory()) {
                        return false;
                    }
                    return "native".equals(name) || "natives".equals(name) || name.startsWith("native-") || name.startsWith("natives-");
                }))
                .map(Arrays::stream)
                .flatMap(Stream::findAny)
                .orElse(root);
    }
}
