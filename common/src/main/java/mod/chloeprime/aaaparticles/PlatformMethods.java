package mod.chloeprime.aaaparticles;

public interface PlatformMethods {
    static PlatformMethods get() {
        return PlatformMethodsImpl.INSTANCE;
    }

    boolean isClientDist();
    default boolean isDedicatedServerDist() {
        return !isClientDist();
    }
    boolean isDatagen();

    /**
     * @return 1=loaded, 0=not loaded, -1=unavailable
     */
    int isModLoaded(String modid);
}
