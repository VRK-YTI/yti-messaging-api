package fi.vm.yti.messaging.configuration;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("terminology")
@Component
@Validated
public class TerminologyProperties {

    @NotNull
    private String publicUrl;

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(final String publicUrl) {
        this.publicUrl = publicUrl;
    }
}
