package fi.vm.yti.messaging.service.impl;

import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import fi.vm.yti.messaging.dto.ResourceDTO;
import fi.vm.yti.messaging.service.SubscriptionService;
import fi.vm.yti.messaging.service.UserService;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserService userService;

    @Inject
    public SubscriptionServiceImpl(final UserService userService) {
        this.userService = userService;
    }

    public ResourceDTO addSubscription(final String uri,
                                       final String type,
                                       final UUID userId) {
        return userService.addResourceToUser(uri, type, userId);
    }

    public ResourceDTO getSubscription(final String uri,
                                       final UUID userId) {
        return userService.getSubscription(uri, userId);
    }

    public ResourceDTO deleteSubscription(final String uri,
                                          final UUID userId) {
        return userService.deleteResourceFromUser(uri, userId);
    }
}
