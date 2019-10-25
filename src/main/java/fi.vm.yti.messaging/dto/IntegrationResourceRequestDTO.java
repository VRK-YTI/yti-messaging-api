package fi.vm.yti.messaging.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement
@XmlType(propOrder = { "container", "pageFrom", "pageSize", "status", "after", "before", "filter", "language", "searchTerm", "pretty", "includeIncompleteFrom", "includeIncomplete", "uri" })
@Schema(name = "Integration resource request DTO", description = "Integration resource request DTO that represents data for HTTP POST body in integration requests.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntegrationResourceRequestDTO {

    private String container;
    private Integer pageFrom;
    private Integer pageSize;
    private List<String> status;
    private String after;
    private String before;
    private List<String> filter;
    private String language;
    private String searchTerm;
    private String pretty;
    private List<String> includeIncompleteFrom;
    private boolean includeIncomplete;
    private List<String> uri;

    public String getContainer() {
        return container;
    }

    public void setContainer(final String container) {
        this.container = container;
    }

    public Integer getPageFrom() {
        return pageFrom;
    }

    public void setPageFrom(final Integer pageFrom) {
        this.pageFrom = pageFrom;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(final Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(final List<String> status) {
        this.status = status;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(final String after) {
        this.after = after;
    }

    public List<String> getFilter() {
        return filter;
    }

    public void setFilter(final List<String> filter) {
        this.filter = filter;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(final String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getPretty() {
        return pretty;
    }

    public void setPretty(final String pretty) {
        this.pretty = pretty;
    }

    public List<String> getIncludeIncompleteFrom() {
        return includeIncompleteFrom;
    }

    public void setIncludeIncompleteFrom(final List<String> includeIncompleteFrom) {
        this.includeIncompleteFrom = includeIncompleteFrom;
    }

    public boolean getIncludeIncomplete() {
        return includeIncomplete;
    }

    public void setIncludeIncomplete(final boolean includeIncomplete) {
        this.includeIncomplete = includeIncomplete;
    }

    public List<String> getUri() {
        return uri;
    }

    public void setUri(final List<String> uri) {
        this.uri = uri;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(final String before) {
        this.before = before;
    }
}
