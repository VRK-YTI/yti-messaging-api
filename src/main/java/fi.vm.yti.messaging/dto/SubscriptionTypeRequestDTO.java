package fi.vm.yti.messaging.dto;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SubscriptionTypeRequestDTO {

    private UUID userId;
    private String subscriptionType;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(final UUID userId) {
        this.userId = userId;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(final String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
}
