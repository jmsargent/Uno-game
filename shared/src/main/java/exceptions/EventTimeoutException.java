package exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * This is thrown when the event times out
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class EventTimeoutException extends BaseException {
    public EventTimeoutException() {
        super(Error.EVENT_TIMEOUT);
    }

    @JsonCreator
    public EventTimeoutException fromJSON(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, EventTimeoutException.class);
    }
}
