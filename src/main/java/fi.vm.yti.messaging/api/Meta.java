package fi.vm.yti.messaging.api;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlType(propOrder = { "code", "message" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "Meta", description = "Meta information model for API responses.")
public class Meta {

    private Integer code;
    private String message;

    public Meta() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(final Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}