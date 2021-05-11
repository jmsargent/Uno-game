package game.controllers;

import game.views.PlayerListView;
import models.PlayerListModel;
import models.PlayerModel;
import network.Command;
import network.CommandUtils;
import observer.Channel;
import observer.EventHub;
import observer.Observer;
import observer.ObserverEvent;

import java.io.IOException;
import java.util.LinkedList;

/**
 * PlayerListController receives requests for updates of PlayerListView and in turn calls on the view to update
 * @author Johanna Schüldt
 * @version 2021-03-05
 */

public class PlayerListController extends Observer {
    private PlayerListView playerListView;
    private PlayerListModel playerListModel;
    private EventHub eventHub;

    /**
     * Constructs a new PlayerListController
     * @param eventHub
     * @param playerListModel
     */
    public PlayerListController(EventHub eventHub, PlayerListModel playerListModel, PlayerListView playerListView){
        this.playerListModel = playerListModel;
        this.playerListView = playerListView;
        this.eventHub = eventHub;
        eventHub.subscribe(this, Channel.PLAYER_LIST);
    }

    /**
     * Handles messages sent from the server and updates the view of players accordingly
     */
    @Override
    public void update() {
        ObserverEvent event = eventHub.getEvent(Channel.PLAYER_LIST);
        Command command = event.getCommand();
        if (command.getName().equals("sync")) {
            LinkedList<PlayerModel> players = CommandUtils.getPlayers(command);
            playerListModel.getPlayers().clear();
            playerListModel.getPlayers().addAll(players);
            playerListView.reRender();
            event.finished();
        }
    }

    public PlayerListView getPlayerListView() {
        return playerListView;
    }
}