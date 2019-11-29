package fi.vm.yti.messaging.service;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.messaging.dto.ResourceDTO;

public interface ResourceService {

    ResourceDTO getOrCreateResource(final String uri,
                                    final String type);

    Set<ResourceDTO> getResourcesForApplication(final String application);

    Set<String> getResourceUrisForApplication(final String application);

    Set<String> getResourceUrisForApplicationAndUserId(final String application,
                                                       final UUID userId);
}
