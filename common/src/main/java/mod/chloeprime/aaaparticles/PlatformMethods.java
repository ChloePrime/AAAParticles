package mod.chloeprime.aaaparticles;

public interface PlatformMethods {
    static PlatformMethods get() {
        return PlatformMethodsImpl.INSTANCE;
    }

    boolean isForge();
    boolean isFabric();
    boolean isModLoaded(String modid);

    boolean isClientDist();
    default boolean isDedicatedServerDist() {
        return !isClientDist();
    }
    boolean isDatagen();
}
