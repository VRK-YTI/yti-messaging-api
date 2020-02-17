package fi.vm.yti.messaging.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import fi.vm.yti.messaging.dto.ResourceDTO;

@Service
public interface SubscriptionService {

    ResourceDTO addSubscription(final String uri,
                                final String type,
                                final UUID userId);

    ResourceDTO getSubscription(final String uri,
                                final UUID userId);

    ResourceDTO deleteSubscription(final String uri,
                                   final UUID userId);
}
