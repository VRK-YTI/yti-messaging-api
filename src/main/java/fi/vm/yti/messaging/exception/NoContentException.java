package fi.vm.yti.messaging.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import fi.vm.yti.messaging.dto.ErrorModel;
import static fi.vm.yti.messaging.exception.ErrorConstants.ERR_MSG_USER_204;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class NoContentException extends YtiMessagingException {

    public NoContentException() {
        super(new ErrorModel(HttpStatus.NO_CONTENT.value(), ERR_MSG_USER_204));
    }
}
