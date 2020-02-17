package fi.vm.yti.messaging.service;

import java.util.Set;

import fi.vm.yti.messaging.dto.IntegrationResponseDTO;

public interface IntegrationService {

    IntegrationResponseDTO getIntegrationContainers(final String applicationIdentifier,
                                                    final Set<String> containerUris);

    IntegrationResponseDTO getIntegrationContainers(final String applicationIdentifier,
                                                    final Set<String> containerUris,
                                                    final boolean fetchDateRangeChanges,
                                                    final boolean getLatest);

    IntegrationResponseDTO getIntegrationResources(final String applicationIdentifier,
                                                   final String containerUri,
                                                   final boolean fetchDateRangeChanges,
                                                   final boolean getLatest);
}
