package fi.vm.yti.messaging.security;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public interface AuthorizationManager {

    boolean canGetUserInformation();

    boolean canAddSubscription();

    boolean isSuperUser();

    UUID getUserId();
}
