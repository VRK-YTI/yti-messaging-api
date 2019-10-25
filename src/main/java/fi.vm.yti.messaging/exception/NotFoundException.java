package fi.vm.yti.messaging.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import fi.vm.yti.messaging.dto.ErrorModel;
import static fi.vm.yti.messaging.exception.ErrorConstants.ERR_MSG_USER_404;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends YtiMessagingException {

    public NotFoundException() {
        super(new ErrorModel(HttpStatus.NOT_FOUND.value(), ERR_MSG_USER_404));
    }
}
