package fi.vm.yti.messaging.api;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement
@Schema(name = "ResponseWrapper", description = "Response wrapper API responses.")
@XmlType(propOrder = { "meta" })
public class ResponseWrapper<T> {

    private Meta meta;

    private Set<T> results;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(final Meta meta) {
        this.meta = meta;
    }
}
