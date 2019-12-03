package fi.vm.yti.messaging.dto;

import java.util.List;

public class UserNotificationDTO {

    private List<IntegrationResourceDTO> codelistResources;
    private List<IntegrationResourceDTO> datamodelResouces;
    private List<IntegrationResourceDTO> terminologyResources;
    private List<IntegrationResourceDTO> commentsResources;

    public UserNotificationDTO(final List<IntegrationResourceDTO> codelistResources,
                               final List<IntegrationResourceDTO> datamodelResouces,
                               final List<IntegrationResourceDTO> terminologyResources,
                               final List<IntegrationResourceDTO> commentsResources) {
        this.codelistResources = codelistResources;
        this.datamodelResouces = datamodelResouces;
        this.terminologyResources = terminologyResources;
        this.commentsResources = commentsResources;
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

    public List<IntegrationResourceDTO> getCommentsResources() {
        return commentsResources;
    }
}
