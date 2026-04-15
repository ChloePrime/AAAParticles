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
}
