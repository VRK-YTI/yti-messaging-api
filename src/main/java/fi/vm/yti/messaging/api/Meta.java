package fi.vm.yti.messaging.api;

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlType(propOrder = { "code", "message", "pageSize", "from", "resultCount", "totalResults", "after", "before" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "Meta", description = "Meta information model for API responses.")
public class Meta {

    private Integer code;
    private String message;
    private Integer pageSize;
    private Integer from;
    private Integer resultCount;
    private Integer totalResults;
    private Date after;
    private Date before;

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

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(final Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(final Integer from) {
        this.from = from;
    }

    public Integer getResultCount() {
        return resultCount;
    }

    public void setResultCount(final Integer resultCount) {
        this.resultCount = resultCount;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(final Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Date getAfter() {
        return after;
    }

    public void setAfter(final Date after) {
        this.after = after;
    }

    public Date getBefore() {
        return before;
    }

    public void setBefore(final Date before) {
        this.before = before;
    }
}
