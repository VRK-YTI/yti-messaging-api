package fi.vm.yti.messaging.exception.exceptionmapping;

import javax.ws.rs.core.Response;

import fi.vm.yti.messaging.api.Meta;
import fi.vm.yti.messaging.api.ResponseWrapper;
import fi.vm.yti.messaging.exception.YtiMessagingException;

interface BaseExceptionMapper {

    default Response getResponse(final YtiMessagingException e) {
        final ResponseWrapper wrapper = new ResponseWrapper();
        final Meta meta = new Meta();
        meta.setMessage(e.getErrorModel().getMessage());
        meta.setCode(e.getErrorModel().getHttpStatusCode());
        wrapper.setMeta(meta);
        return Response.status(e.getErrorModel().getHttpStatusCode()).entity(wrapper).build();
    }
}
