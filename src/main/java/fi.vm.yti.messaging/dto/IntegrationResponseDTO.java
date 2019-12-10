package fi.vm.yti.messaging.dto;

import java.util.List;

import fi.vm.yti.messaging.api.Meta;

public class IntegrationResponseDTO {

    private Meta meta;
    private List<IntegrationResourceDTO> results;

    public IntegrationResponseDTO() {
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(final Meta meta) {
        this.meta = meta;
    }

    public List<IntegrationResourceDTO> getResults() {
        return results;
    }

    public void setResults(final List<IntegrationResourceDTO> results) {
        this.results = results;
    }
}
