package fi.vm.yti.messaging.security.impl;

import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import fi.vm.yti.messaging.security.AuthorizationManager;
import fi.vm.yti.security.AuthenticatedUserProvider;

@Service
public class AuthorizationManagerImpl implements AuthorizationManager {

    private final AuthenticatedUserProvider userProvider;

    @Inject
    AuthorizationManagerImpl(final AuthenticatedUserProvider userProvider) {
        this.userProvider = userProvider;
    }

    public boolean canAddSubscription() {
        return !userProvider.getUser().isAnonymous();
    }

    public boolean canGetUserInformation() {
        return !userProvider.getUser().isAnonymous();
    }

    public UUID getUserId() {
        return userProvider.getUser().getId();
    }
}
