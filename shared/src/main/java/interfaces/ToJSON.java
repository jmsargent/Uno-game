package interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * All models that are sent to the server needs this to be able to be serialized into a JSON string
 */
public interface ToJSON {
    /**
     * @return The JSON represenation of the object
     */
    default String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
