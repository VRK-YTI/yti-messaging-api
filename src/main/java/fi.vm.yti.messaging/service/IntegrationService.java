package fi.vm.yti.messaging.service;

import java.util.Set;

import fi.vm.yti.messaging.dto.IntegrationResponseDTO;

public interface IntegrationService {

    IntegrationResponseDTO getIntegrationContainers(final String applicationIdentifier,
                                                    final Set<String> containerUris,
                                                    final boolean fetchDateRangeChanges);

    IntegrationResponseDTO getIntegrationResources(final String applicationIdentifier,
                                                   final String containerUri,
                                                   final boolean fetchDateRangeChanges);
}
