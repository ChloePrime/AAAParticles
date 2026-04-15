package mod.chloeprime.aaaparticles.client;

import java.util.ServiceLoader;

class ClientPlatformMethodsImpl {
    static final ClientPlatformMethods INSTANCE = ServiceLoader.load(ClientPlatformMethods.class)
            .findFirst()
            .orElseThrow(ExceptionInInitializerError::new);
}
