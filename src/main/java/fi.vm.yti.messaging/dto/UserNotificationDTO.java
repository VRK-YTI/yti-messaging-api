package fi.vm.yti.messaging.dto;

import java.util.Set;

public class UserNotificationDTO {

    private Set<IntegrationResourceDTO> codelistResources;
    private Set<IntegrationResourceDTO> datamodelResouces;
    private Set<IntegrationResourceDTO> terminologyResources;
    private Set<IntegrationResourceDTO> commentsResources;

    public UserNotificationDTO(final Set<IntegrationResourceDTO> codelistResources,
                               final Set<IntegrationResourceDTO> datamodelResouces,
                               final Set<IntegrationResourceDTO> terminologyResources,
                               final Set<IntegrationResourceDTO> commentsResources) {
        this.codelistResources = codelistResources;
        this.datamodelResouces = datamodelResouces;
        this.terminologyResources = terminologyResources;
        this.commentsResources = commentsResources;
    }

    public Set<IntegrationResourceDTO> getCodelistResources() {
        return codelistResources;
    }

    public void setCodelistResources(final Set<IntegrationResourceDTO> codelistResources) {
        this.codelistResources = codelistResources;
    }

    public Set<IntegrationResourceDTO> getDatamodelResouces() {
        return datamodelResouces;
    }

    public void setDatamodelResouces(final Set<IntegrationResourceDTO> datamodelResouces) {
        this.datamodelResouces = datamodelResouces;
    }

    public Set<IntegrationResourceDTO> getTerminologyResources() {
        return terminologyResources;
    }

    public void setTerminologyResources(final Set<IntegrationResourceDTO> terminologyResources) {
        this.terminologyResources = terminologyResources;
    }

    public Set<IntegrationResourceDTO> getCommentsResources() {
        return commentsResources;
    }

    public void setCommentsResources(final Set<IntegrationResourceDTO> commentsResources) {
        this.commentsResources = commentsResources;
    }
}
