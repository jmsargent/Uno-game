package game;

import game.controllers.*;
import game.views.*;
import models.*;
import observer.Channel;
import observer.EventHub;
import observer.Observer;
import observer.ObserverEvent;
import tools.Settings;

import java.util.LinkedList;
import java.util.UUID;

/**
 * Gamecontroller is the whole game, all parts collected together.
 *
 * @author everyone
 * @version 2021-03-07
 * TODO: Clean up this class, delete test constructor & unused methods!
 */
public class GameController extends Observer {
    private GameModel gameModel = new GameModel();
    private GameView gameView;

    private ColorPickerView colorPickerView = new ColorPickerView();

    private DrawPileController drawPileController;
    private DrawPileView drawPileView;
    private DrawPile drawPileModel;
    private PlayPileController playPileController;
    private PlayPileView playPileView;
    private PlayPile playPileModel;

    //private InGameMenuController inGameMenuController;
    private PlayerListController playerListController;
    private PlayerListModel playerListModel;
    private PlayerListView playerListView;

    private PlayerModel playerModel;
    private PlayerView playerView;
    private PlayerController playerController;

    private String gameId;
    private UUID clientID;
    private EventHub eventHub;
    private InGameMenuController inGameMenuController;


    public GameController(EventHub eventHub, Settings settings) {
        this.eventHub = eventHub;
        playerModel = new PlayerModel(settings.getPlayerName(), new CardPile(), settings.getUuid(), false);
        this.clientID = settings.getUuid();
        playerView = new PlayerView(playerModel);
        playerListModel = new PlayerListModel(new LinkedList<>());
        playerListView = new PlayerListView(playerListModel);
        playerListController = new PlayerListController(eventHub, playerListModel, playerListView);
        init();
        this.gameView = new GameView(this.gameModel, this.playerView, this.playPileView,
                this.drawPileView, this.colorPickerView, this.inGameMenuController.getView(), this.playerListView);
        eventHub.subscribe(this, Channel.GAME_CONTROLLER);
    }

    /**
     * Inits all of the subcomponents of a game
     */
    private void init() {

        ColorPickerController colorPickerController = new ColorPickerController(eventHub);

        //get the drawpile controller and view
        drawPileController = new DrawPileController(eventHub);
        this.drawPileView = drawPileController.getDrawPileView();
        this.drawPileModel = drawPileController.getDrawPileModel();

        //get the playpile
        playPileController = new PlayPileController(eventHub);
        this.playPileView = playPileController.getPlayPileView();
        this.playPileModel = playPileController.getPlayPileModel();

        initPlayer();
        //opponentModel = new OpponentModel();
        //opponentsController = new OpponentController(eventHub, opponentModel);

        //ingameMenu
        this.inGameMenuController = new InGameMenuController(eventHub);
    }

    /**
     * Everything to do with the players hand
     */
    private void initPlayer() {
        playerController = new PlayerController(this.playerModel, this.playerView, eventHub, clientID);
    }

    /**
     * if a player wins the game (pressed uno before), the game stops and a window pops up with text
     * "Player x won the game" or similar
     */
    public void gameFinished() {
        //this.close(); ???
        this.gameView.gameIsWon();
    }

    /**
     * reverse player order,
     */
    public void reversePlayerOrder() {
    }

    /**
     * When a player has played their card, automatically update player turn.
     */
    public void nextPlayersTurn() {
    }

    /**
     * Update currPlayer, to the name of the player whose turn it is right now
     */
    public void currentPlayersTurn() {
    }

    public void playCard() {
    }

    public void drawCard() {
    }

    public void saveGame() {
    }

    /**
     * start the game
     */
    private void startGame() {
        this.inGameMenuController.gameStarted();
    }

    /**
     * close the game (gameview)
     */
    public void close() {
        this.gameView.dispose();
    }

    /**
     * get events from the GAME_CONTROLLER channel and react to the message, for example: start the game.
     */
    @Override
    public void update() {
        ObserverEvent event = eventHub.getEvent(Channel.GAME_CONTROLLER);
        switch (event.getCommand().getName()) {
            case "game_started" -> startGame();
            case "won_game" -> gameFinished();
            default -> System.out.println("GameController: Unknown command: " + event.getCommand().getName());
        }
    }

}
