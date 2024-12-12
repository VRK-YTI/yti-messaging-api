package fi.vm.yti.messaging.dto;

import java.util.List;

public class UserNotificationDTO {

    private List<IntegrationResourceDTO> codelistResources;
    private List<IntegrationResourceDTO> datamodelResouces;
    private List<IntegrationResourceDTO> terminologyResources;

    public UserNotificationDTO(final List<IntegrationResourceDTO> codelistResources,
                               final List<IntegrationResourceDTO> datamodelResouces,
                               final List<IntegrationResourceDTO> terminologyResources) {
        this.codelistResources = codelistResources;
        this.datamodelResouces = datamodelResouces;
        this.terminologyResources = terminologyResources;
    }

    public List<IntegrationResourceDTO> getCodelistResources() {
        return codelistResources;
    }

    public List<IntegrationResourceDTO> getDatamodelResouces() {
        return datamodelResouces;
    }

    public List<IntegrationResourceDTO> getTerminologyResources() {
        return terminologyResources;
    }
}
