package fi.vm.yti.messaging.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import fi.vm.yti.messaging.dto.ErrorModel;
import static fi.vm.yti.messaging.exception.ErrorConstants.ERR_MSG_USER_304;

@ResponseStatus(HttpStatus.NOT_MODIFIED)
public class NotModifiedException extends YtiMessagingException {

    public NotModifiedException() {
        super(new ErrorModel(HttpStatus.NOT_MODIFIED.value(), ERR_MSG_USER_304));
    }
}
