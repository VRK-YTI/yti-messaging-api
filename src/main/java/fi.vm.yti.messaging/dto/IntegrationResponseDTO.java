package fi.vm.yti.messaging.dto;

import java.util.Set;

import fi.vm.yti.messaging.api.Meta;

public class IntegrationResponseDTO {

    private Meta meta;
    private Set<IntegrationResourceDTO> results;

    public IntegrationResponseDTO() {
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(final Meta meta) {
        this.meta = meta;
    }

    public Set<IntegrationResourceDTO> getResults() {
        return results;
    }

    public void setResults(final Set<IntegrationResourceDTO> results) {
        this.results = results;
    }
}
