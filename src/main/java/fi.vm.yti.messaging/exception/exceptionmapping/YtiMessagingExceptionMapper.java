package fi.vm.yti.messaging.exception.exceptionmapping;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.yti.messaging.exception.YtiMessagingException;

@Provider
public class YtiMessagingExceptionMapper implements BaseExceptionMapper, ExceptionMapper<YtiMessagingException> {

    @Override
    public Response toResponse(final YtiMessagingException e) {
        return getResponse(e);
    }
}
