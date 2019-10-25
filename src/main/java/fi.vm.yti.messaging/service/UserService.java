package fi.vm.yti.messaging.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import fi.vm.yti.messaging.dto.ResourceDTO;
import fi.vm.yti.messaging.dto.UserDTO;

@Service
public interface UserService {

    ResourceDTO addResourceToUser(final String uri,
                                  final String type,
                                  final UUID userId);

    ResourceDTO getSubscription(final String uri,
                                final UUID userId);

    ResourceDTO deleteResourceFromUser(final String uri,
                                       final UUID userId);

    UserDTO setSubscriptionType(final UUID userId,
                                final String subscriptionType);

    UserDTO getOrCreateUser(final UUID userId);

    UserDTO findById(final UUID userId);

    Set<UserDTO> findAll();
}
