package tools;

import network.Command;
import network.CommandFactory;
import network.CommandUtils;
import network.Connection;
import observer.Channel;
import observer.EventHub;
import observer.Observer;
import observer.ObserverEvent;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A library for clients to communicate with server
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class CommunicationCaptain extends Observer implements Runnable {
    private Connection connection;
    private Settings settings;
    private EventHub observer;
    private Thread thread;
    private LinkedBlockingQueue<Boolean> activeGamesDone = new LinkedBlockingQueue<>();
    private ActiveGame[] activeGames;
    private LinkedBlockingQueue<Boolean> savedGamesDone = new LinkedBlockingQueue<>();
    private SavedGame[] savedGames;

    public CommunicationCaptain(Settings settings, EventHub eventHub) {
        this.settings = settings;
        this.observer = eventHub;
        this.eventHub = eventHub;
        eventHub.subscribe(this, Channel.COMMUNICATION_CAPTAIN);
    }

    /**
     * Open the connection if its not open
     *
     * @throws IOException If the server comms failed
     */
    private void connect() throws IOException {
        if (connection == null || !connection.isConnected()) {
            connection = new Connection(settings.getServerHost(), settings.getServerPort());
        }
    }

    /**
     * Tell the server to start a new game session
     *
     * @throws IOException If the server comms failed
     */
    public void newGame() throws IOException {
        connect();
        Command newGame = CommandFactory.newGame(settings.getPlayerName());
        send(newGame);
    }

    /**
     * Send a message to the server while also attaching the clientID to the command
     *
     * @throws IOException If the server comms failed
     */
    private void send(Command command) throws IOException {
        command.setSender(settings.getUuid());
        connection.write(command);
    }

    /**
     * Get a list of active games from the server
     *
     * @throws IOException If the server comms failed
     */
    public ActiveGame[] getActiveGames() throws IOException, InterruptedException {
        connect();
        activeGamesDone.clear();
        Command getActiveGames = CommandFactory.getActiveGames();
        send(getActiveGames);
        activeGamesDone.poll(60, TimeUnit.SECONDS);
        return activeGames;
    }

    /**
     * Get a list of saved games from the server
     *
     * @throws IOException If the server comms failed
     */
    public SavedGame[] getSavedGames() throws IOException, InterruptedException {
        connect();
        savedGamesDone.clear();
        Command getSavedGames = CommandFactory.getSavedGames(settings.getUuid());
        send(getSavedGames);
        savedGamesDone.poll(60, TimeUnit.SECONDS);
        return savedGames;
    }

    /**
     * Tell the server to start the game session
     *
     * @throws IOException If the server comms failed
     */
    private void startGame() throws IOException {
        connect();
        Command startGame = CommandFactory.startGame();
        send(startGame);
    }

    /**
     * Tell the server that the client is disconnecting
     *
     * @throws IOException If the server comms failed
     */
    private void disconnect() throws IOException {
        connect();
        Command disconnect = CommandFactory.disconnect();
        send(disconnect);
    }

    /**
     * Tell the server to save the session
     *
     * @throws IOException If the server comms failed
     */
    private void save() throws IOException {
        connect();
        Command save = CommandFactory.save();
        send(save);
    }

    /**
     * The thread used to receive data from the server
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                connect();
                Command command = connection.read();
                switch (command.getName().toLowerCase()) {
                    case "game_joined" -> eventHub.addEvent(new ObserverEvent(command), Channel.APP);
                    case "game_started" -> eventHub.addEvent(new ObserverEvent(CommandFactory.gameStarted()), Channel.GAME_CONTROLLER);
                    case "drawn_cards" -> eventHub.addEvent(new ObserverEvent(command), Channel.PLAYER);
                    case "active_games" -> handleActiveGamesResponse(command);
                    case "saved_games" -> handleSavedGamesResponse(command);
                    case "player_turn" -> {
                        eventHub.addEvent(new ObserverEvent(CommandFactory.setPlayerTurn(command.getArgs().get("player.id").equals(settings.getUuid().toString()))), Channel.PLAYER);
                        eventHub.addEvent(new ObserverEvent(CommandFactory.setPlayerTurn(command.getArgs().get("player.id").equals(settings.getUuid().toString()))), Channel.DRAW_PILE);
                    }
                    case "sync" -> {
                        eventHub.addEvent(new ObserverEvent(command), Channel.DRAW_PILE);
                        eventHub.addEvent(new ObserverEvent(command), Channel.PLAY_PILE);
                        eventHub.addEvent(new ObserverEvent(command), Channel.PLAYER_LIST);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the returned active games and tells the other thread that the fetch is completed
     *
     * @param command This object contains all the event data
     */
    private void handleActiveGamesResponse(Command command) {
        activeGames = CommandUtils.getActiveGames(command);
        activeGamesDone.add(true);
    }

    /**
     * Sets the returned saved games and tells the other thread that the fetch is completed
     *
     * @param command This object contains all the event data
     */
    private void handleSavedGamesResponse(Command command) {
        savedGames = CommandUtils.getSavedGames(command);
        savedGamesDone.add(true);
    }

    /**
     * @author Jonathan Sargent , Martin Claesson
     * @version 2021-03-05
     */
    @Override
    public void update() {
        try {
            ObserverEvent event = eventHub.getEvent(Channel.COMMUNICATION_CAPTAIN);
            switch (event.getCommand().getName()) {
                case "start_game" -> startGame();
                case "disconnect" -> disconnect();
                case "join_game", "play_card", "draw_card", "draw_2", "draw_4", "change_color_response" -> send(event.getCommand());
                case "save" -> save();
                default -> System.out.println("Unknown command: " + event.getCommand().getName());
            }
            event.finished();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start the receiver thread
     */
    public void start() {
        this.thread = new Thread(this);
        this.thread.start();
    }
}
