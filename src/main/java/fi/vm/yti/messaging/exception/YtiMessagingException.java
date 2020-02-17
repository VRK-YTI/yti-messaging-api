package fi.vm.yti.messaging.exception;

import fi.vm.yti.messaging.dto.ErrorModel;

public class YtiMessagingException extends RuntimeException {

    private final ErrorModel errorModel;

    public YtiMessagingException(final ErrorModel errorModel) {
        super(errorModel.getMessage());
        this.errorModel = errorModel;
    }

    public ErrorModel getErrorModel() {
        return errorModel;
    }
}
