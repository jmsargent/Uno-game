package tools;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import interfaces.IGameSession;
import interfaces.ToJSON;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * This represent a current game session
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class ActiveGame extends IGameSession implements ToJSON {
    private boolean inProgress;

    public ActiveGame(UUID id, String gameName, HashMap<String, String> players, boolean inProgress) {
        super(id, gameName, players);
        this.inProgress = inProgress;
    }

    public ActiveGame() {}

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    /**
     * Convert a JSON string into this object type
     *
     * @param json the json string
     * @return a new Command object
     * @throws IOException if something goes wrong while converting from JSON this error will be thrown
     */
    @JsonCreator
    public ActiveGame fromJSON(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, ActiveGame.class);
    }
}
