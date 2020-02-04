package fi.vm.yti.messaging.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class ErrorModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private int httpStatusCode;
    private String message;
    private String entityIdentifier;
    private String nonTranslatableMessage;

    public ErrorModel(int httpStatusCode,
                      String message) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

    public ErrorModel(int httpStatusCode,
                      String message,
                      String entityIdentifier) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
        this.entityIdentifier = entityIdentifier;
    }

    public ErrorModel(int httpStatusCode,
                      String message,
                      String entityIdentifier,
                      String nonTranslatableMessage) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
        this.entityIdentifier = entityIdentifier;
        this.nonTranslatableMessage = nonTranslatableMessage;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getEntityIdentifier() {
        return this.entityIdentifier;
    }

    public void setEntityIdentifier(String entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }

    public String getNonTranslatableMessage() {
        return this.nonTranslatableMessage;
    }

    public void setNonTranslatableMessage(String nonTranslatableMessage) {
        this.nonTranslatableMessage = nonTranslatableMessage;
    }
}
