package fi.vm.yti.messaging.dto;

import java.util.Set;
import java.util.UUID;

public class UserDTO {

    private UUID id;
    private String subscriptionType;
    private Set<ResourceDTO> resources;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(final String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Set<ResourceDTO> getResources() {
        return resources;
    }

    public void setResources(final Set<ResourceDTO> resources) {
        this.resources = resources;
    }
}
