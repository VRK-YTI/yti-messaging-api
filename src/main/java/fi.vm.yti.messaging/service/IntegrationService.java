package fi.vm.yti.messaging.service;

import java.util.Set;

import fi.vm.yti.messaging.dto.IntegrationResourceDTO;

public interface IntegrationService {

    Set<IntegrationResourceDTO> getIntegrationContainers(final String applicationIdentifier,
                                                         final Set<String> containerUris,
                                                         final boolean fetchDateRangeChanges);

    Set<IntegrationResourceDTO> getIntegrationResources(final String applicationIdentifier,
                                                        final String containerUri,
                                                        final boolean fetchDateRangeChanges);
}
