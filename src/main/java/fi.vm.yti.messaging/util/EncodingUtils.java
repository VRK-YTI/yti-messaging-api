package fi.vm.yti.messaging.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.springframework.http.HttpStatus;

import fi.vm.yti.messaging.dto.ErrorModel;
import fi.vm.yti.messaging.exception.YtiMessagingException;
import static fi.vm.yti.messaging.exception.ErrorConstants.ERR_MSG_USER_406;

public interface EncodingUtils {

    static String urlDecodeString(final String string) {
        try {
            return URLDecoder.decode(string, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new YtiMessagingException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_406));
        }
    }
}
