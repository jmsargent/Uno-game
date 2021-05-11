package game;

import models.PlayerModel;

/**
 * Local player information
 * @author everyone
 * @version 2021-03-07
 */
public class GameModel {
    private String gameId;
    private PlayerModel localPlayer;

    public GameModel() {
    }

    public PlayerModel getLocalPlayer() {
        return localPlayer;
    }

    public void setLocalPlayer(PlayerModel localPlayer) {
        this.localPlayer = localPlayer;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

}
