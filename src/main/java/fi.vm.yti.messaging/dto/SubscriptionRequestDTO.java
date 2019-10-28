package fi.vm.yti.messaging.dto;

public class SubscriptionRequestDTO {

    private String action;
    private String type;
    private String uri;

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public String getAction() {
        return action;
    }

    public void setAction(final String action) {
        this.action = action;
    }
}
