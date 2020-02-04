package fi.vm.yti.messaging.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import fi.vm.yti.messaging.dto.ErrorModel;
import static fi.vm.yti.messaging.exception.ErrorConstants.ERR_MSG_USER_401;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class UnauthorizedException extends YtiMessagingException {

    public UnauthorizedException() {
        super(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
    }
}
