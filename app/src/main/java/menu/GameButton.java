package menu;

import interfaces.IGameSession;

import javax.swing.*;

public class GameButton extends JButton {
    private IGameSession gameSession;

    public GameButton(IGameSession gameSession) {
        super(gameSession.getGameName());
        this.gameSession = gameSession;
    }

    public IGameSession getGameSession() {
        return gameSession;
    }

    public void setGameSession(IGameSession gameSession) {
        this.gameSession = gameSession;
    }
}
