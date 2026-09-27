package mod.chloeprime.aaaparticles;

public interface PlatformMethods {
    static PlatformMethods get() {
        return PlatformMethodsImpl.INSTANCE;
    }

    boolean isForge();
    boolean isFabric();

    boolean isClientDist();
    default boolean isDedicatedServerDist() {
        return !isClientDist();
    }
    boolean isDatagen();

    /**
     * @return 1=loaded, 0=not loaded, -1=unavailable
     */
    int isModLoaded(String modid);

    default boolean isModLoadedFailFast(String modid) {
        var ret = isModLoaded(modid);
        if (ret < 0) {
            throw new IllegalStateException("Accessing isModLoaded too early");
        }
        return ret > 0;
    }
}
