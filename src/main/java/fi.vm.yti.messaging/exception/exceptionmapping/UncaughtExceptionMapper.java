package fi.vm.yti.messaging.exception.exceptionmapping;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@Provider
public class UncaughtExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(UncaughtExceptionMapper.class);

    @Override
    public Response toResponse(final Exception e) {
        LOG.error("UnCaughtException occurred: ", e);
        return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
    }
}
