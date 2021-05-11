package game.views;

import models.PlayerListModel;
import models.PlayerModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

/**
 * PlayerListView is a view of all players
 * @author Johanna Schüldt
 * @version 2021-03-06
 */

public class PlayerListView extends JPanel {
    private PlayerListModel playerListModel;
    private LinkedList<PlayerModel> players;
    private HashMap<UUID, PlayerCardView> playerCardViews = new HashMap<>();

    /**
     * Constructs a new PlayerListView
     */
    public PlayerListView(PlayerListModel playerListModel){
        this.playerListModel = playerListModel;
        reRender();
    }

    public void reRender() {
        players = playerListModel.getPlayers();
        setLayout(new FlowLayout());
        if(playerCardViews.isEmpty()) {
            for (PlayerModel player : players) {
                PlayerCardView playerCardView = new PlayerCardView(player);
                this.add(playerCardView);
                playerCardViews.put(player.getId(), playerCardView);
            }
        }
        else {
            for (PlayerModel player : players) {
                PlayerCardView pcv = playerCardViews.get(player.getId());
                pcv.updatePlayerTurn(player);
                pcv.updateUno(player);
                pcv.updateCards(player);
            }
        }
        repaint(); //TODO kanske inte behövs
    }
}

