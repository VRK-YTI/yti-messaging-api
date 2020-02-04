package fi.vm.yti.messaging.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SubscriptionResponseDTO {

    private GroupManagementUserDTO user;
    private List<ResourceDTO> resources;

    public GroupManagementUserDTO getUser() {
        return user;
    }

    public void setUser(final GroupManagementUserDTO user) {
        this.user = user;
    }

    public List<ResourceDTO> getResources() {
        return resources;
    }

    public void setResources(final List<ResourceDTO> resources) {
        this.resources = resources;
    }
}
