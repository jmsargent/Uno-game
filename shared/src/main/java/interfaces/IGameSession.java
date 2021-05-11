package interfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This abstract class is used by ActiveGame and SavedGame, and represent a GameSession in either state
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public abstract class IGameSession {
    private UUID id;
    private String gameName;
    private HashMap<String,String> players;

    protected IGameSession(UUID id, String gameName, HashMap<String,String> players) {
        this.id = id;
        this.gameName = gameName;
        this.players = players;
    }

    protected IGameSession() {}

    /**
     * @return A list of all players in a ID:Name hashmap
     */
    public Map<String, String> getPlayers() {
        return players;
    }

    /**
     * @return the game ID
     */
    public String getId() {
        return id.toString();
    }

    /**
     * @return The name of the game
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * @param id Set the game ID
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @param gameName the name of the game
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * Set the player list
     * @param players a hashmap in a ID:Name format
     */
    public void setPlayers(HashMap<String, String> players) {
        this.players = players;
    }
}
