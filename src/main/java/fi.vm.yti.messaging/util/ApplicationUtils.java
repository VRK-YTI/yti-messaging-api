package fi.vm.yti.messaging.util;

import org.springframework.http.HttpStatus;

import fi.vm.yti.messaging.dto.ErrorModel;
import fi.vm.yti.messaging.exception.YtiMessagingException;
import static fi.vm.yti.messaging.api.ApiConstants.*;

public interface ApplicationUtils {

    String TYPE_CODELIST = "codelist";
    String TYPE_LIBRARY = "library";
    String TYPE_PROFILE = "profile";
    String TYPE_TERMINOLOGY = "terminology";
    String TYPE_COMMENTROUND = "commentround";
    String TYPE_COMMENTTHREAD = "commentthread";

    static String getApplicationByType(final String type) {
        switch (type) {
            case TYPE_CODELIST:
                return APPLICATION_CODELIST;
            case TYPE_LIBRARY:
            case TYPE_PROFILE:
                return APPLICATION_DATAMODEL;
            case TYPE_TERMINOLOGY:
                return APPLICATION_TERMINOLOGY;
            case TYPE_COMMENTROUND:
            case TYPE_COMMENTTHREAD:
                return APPLICATION_COMMENTS;
            default:
                throw new YtiMessagingException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unknown type in resource: " + type));
        }
    }
}
