package fi.vm.yti.messaging.service;

import java.util.Map;

import fi.vm.yti.messaging.dto.IntegrationResourceDTO;

public interface ContainerNameService {

    void refreshPrefLabels();

    void addPrefLabel(final IntegrationResourceDTO integrationResource);

    void addPrefLabelToUriWithType(final String uri,
                                   final String type);

    Map<String, String> getPrefLabel(final String uri);
}
