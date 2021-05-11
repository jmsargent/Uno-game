package exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * This exception is throw if the card in question is invalid for some reason
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class InvalidCardException extends BaseException {
    public InvalidCardException() {
        super(Error.INVALID_CARD);
    }

    @JsonCreator
    public InvalidCardException fromJSON(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, InvalidCardException.class);
    }
}
