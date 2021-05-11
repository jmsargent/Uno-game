package tools;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import interfaces.IGameSession;
import interfaces.ToJSON;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * This represents an old game session that is saved
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class SavedGame extends IGameSession implements ToJSON {
    private Date lastPlayed;

    public SavedGame(UUID id, String gameName, HashMap<String, String> players, Date lastPlayed) {
        super(id, gameName, players);
        this.lastPlayed = lastPlayed;
    }

    public SavedGame() {     
    }

    public Date getLastPlayed() {
        return lastPlayed;
    }

    /**
     * Convert a JSON string into this object type
     *
     * @param json the json string
     * @return a new Command object
     * @throws IOException if something goes wrong while converting from JSON this error will be thrown
     */
    @JsonCreator
    public SavedGame fromJSON(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, SavedGame.class);
    }
}
