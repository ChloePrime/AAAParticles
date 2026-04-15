package mod.chloeprime.aaaparticles;

import java.util.ServiceLoader;

class PlatformMethodsImpl {
    static final PlatformMethods INSTANCE = ServiceLoader.load(PlatformMethods.class)
            .findFirst()
            .orElseThrow(ExceptionInInitializerError::new);
}
