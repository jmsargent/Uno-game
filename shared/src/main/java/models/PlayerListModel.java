package models;

import java.util.LinkedList;

/**
 * PlayerListModel contains a list of all players in a game
 * @author Johanna Schüldt
 * @version 2021-03-05
 */


public class PlayerListModel {

    private LinkedList<PlayerModel> players;


    /**
     * Constructs a new PlayerListModel
     */
    public PlayerListModel(LinkedList<PlayerModel> players) {
        this.players = players;
    }


    /**
     * @return opponents
     */
    public LinkedList<PlayerModel> getPlayers(){
        return players;
    }

}