package exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * This exception is thrown when the code tries to access an events response before its finished
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class EventNotFinished extends BaseException {
    public EventNotFinished() {
        super(Error.EVENT_NOT_FINISHED);
    }

    @JsonCreator
    public EventNotFinished fromJSON(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, EventNotFinished.class);
    }
}
