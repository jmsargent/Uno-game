package exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * This is thrown when someone wants to join a non existent game
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class InvalidGameSessionException extends BaseException {
    public InvalidGameSessionException() {
        super(Error.NO_SUCH_GAME_SESSION);
    }

    @JsonCreator
    public InvalidGameSessionException fromJSON(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, InvalidGameSessionException.class);
    }
}
