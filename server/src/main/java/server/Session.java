package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import exceptions.Error;
import exceptions.InvalidGameSessionException;
import models.Card;
import models.DrawPile;
import models.PlayPile;
import models.PlayerModel;
import network.Command;
import network.CommandFactory;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The game session thread object
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class Session implements Runnable {
    private UUID id;
    private String name;
    private final LinkedList<PlayerConnection> players;
    private final LinkedBlockingQueue<PlayerConnection> queue;
    private final LinkedBlockingQueue<Command> messageQueue = new LinkedBlockingQueue<>();
    private boolean inProgress = false;
    private int direction = 1;
    private Thread thread;
    private DrawPile drawPile;
    private PlayPile playPile;
    private PlayerConnection currentPlayer;

    /**
     * Create a session listening to the provided connection queue
     *
     * @param queue the connection queue
     */
    public Session(LinkedBlockingQueue<PlayerConnection> queue) {
        this.id = UUID.randomUUID();
        this.players = new LinkedList<>();
        this.queue = queue;
        System.out.println("New session with id " + id.toString());
    }

    /**
     * @param executeQuery The resultset to base the session on
     * @param queue        the connection queue
     * @throws InvalidGameSessionException No such game exists
     * @throws SQLException                Something went wrong with the SQL
     * @throws IOException                 most likely a JSON parsing error
     */
    public Session(ResultSet executeQuery, LinkedBlockingQueue<PlayerConnection> queue) throws InvalidGameSessionException, SQLException, IOException {
        this(queue);
        if (executeQuery.next()) {
            this.id = UUID.fromString(executeQuery.getString("id"));
            this.drawPile = new DrawPile().fromJSON(executeQuery.getString("draw_pile"));
            this.playPile = new PlayPile().fromJSON(executeQuery.getString("play_pile"));
            //TODO import players from database
        } else {
            throw new InvalidGameSessionException();
        }
    }

    /**
     * Setup the entire game session with all the card piles and give all the players their starting cards
     */
    public void setup() {
        try {
            this.drawPile = new DrawPile();
            this.drawPile.createUNODeck();
            this.playPile = new PlayPile();
            this.playPile.initialize();
            this.currentPlayer = players.get(new Random().nextInt(players.size()));
            for (PlayerConnection pc : players) {
                try {
                    pc.giveCard(drawPile.draw(7));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Command msg = new Command("player_turn", "player.id:" + currentPlayer.getClientId());
            push(msg);
            sync(null);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send the session data to the players to keep them synced
     *
     * @param sender
     * @throws JsonProcessingException in case any data is malformed
     */
    public void sync(UUID sender) throws JsonProcessingException {
        PlayerModel[] playerArray = new PlayerModel[players.size()];
        int i = 0;
        for (PlayerConnection pc : players) {
            pc.getPlayer().setId(pc.getClientId());
            playerArray[i] = pc.getPlayer();
            i++;
        }
        Command command = CommandFactory.gameSync(drawPile, playPile, playerArray);
        command.setSender(sender);
        push(command);
    }

    /**
     * Adopt the playerconnection provided and start a listener thread for it
     *
     * @param pc The playerconnection to adopt
     * @throws IOException if the thread is unreachable
     */
    public void join(PlayerConnection pc) throws IOException {
        players.add(pc);
        Thread thread = new Thread(pc);
        Command command = CommandFactory.connectionConfirmation(id);
        command.setSender(id);
        pc.getConnection().write(command);
        pc.setSession(this);
        pc.setThread(thread);
        thread.start();
        if (players.size() == 1) {
            this.name = pc.getPlayer().getName() + " UNO Game";
        }
    }

    /**
     * @param command Add a message to the message queue
     */
    public void push(Command command) {
        messageQueue.add(command);
    }

    /**
     * The handler for when a player disconnects, and kills the thread if its empty
     *
     * @param playerConnection The disconnected connection
     */
    public void playerDisconnectedEvent(PlayerConnection playerConnection) {
        for (PlayerConnection pc : players)
            if (playerConnection != pc && pc.getConnection().isConnected() && !pc.getThread().isInterrupted())
                return;

        System.out.println(id + " is empty, killing the lobby");
        thread.interrupt();
    }

    /**
     * This is the thread that handles the communication with the clients. If the game has not already started
     * it simply sits and wait for new connections to adopt. While in progress it waits for messages to post to all clients
     */
    @Override
    public void run() {
        System.out.println("Game Session: " + id.toString() + ", has started");
        while (!Thread.currentThread().isInterrupted()) {
            while (!inProgress) {
                PlayerConnection target;
                try {
                    target = queue.poll(1, TimeUnit.SECONDS);
                    if (target == null) {
                        continue;
                    }
                    try {
                        join(target);
                    } catch (IOException e) {
                        target.getConnection().write(Error.FAILED_TO_CONNECT);
                        e.printStackTrace();
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
            while (inProgress) {
                Command command = null;
                try {
                    command = messageQueue.poll(1000, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (command == null) {
                    continue;
                }
                try {
                    System.out.println(command.toJson());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                for (PlayerConnection pc : players) {
                    if (command.getSender() == null || !pc.getClientId().toString().equals(command.getSender().toString())) {
                        try {
                            pc.getConnection().write(command);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println(pc.getClientId().toString() + " is equals to " + command.getSender().toString());
                    }
                }
            }
        }
    }

    /**
     * Below we have some standard getters and setters
     */
    public UUID getId() {
        return id;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public String getName() {
        return name;
    }

    /**
     * @return Get all players in a ID:Playername hashmap
     */
    public HashMap<String, String> getPlayers() {
        HashMap<String, String> players = new HashMap<>();
        for (PlayerConnection pc : this.players) {
            players.put(pc.getClientId().toString(), pc.getPlayer().getName());
        }
        return players;
    }

    /**
     * Dump the current session to the database
     */
    public void save() {
        Database db = new Database();
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO saved_games (id, draw_pile, play_pile) VALUES (?,?,?)")) {
                ps.setString(1, id.toString());
                ps.setString(2, drawPile.toJson());
                ps.setString(3, playPile.toJson());
                if (ps.executeUpdate() > 0) {
                    for (PlayerConnection pc : players) {
                        try (PreparedStatement psPlayers = conn.prepareStatement("INSERT INTO saved_game_players (player_id, player_hand, game_id) VALUES (?,?,?)")) {
                            psPlayers.setString(1, pc.getClientId().toString());
                            psPlayers.setString(2, pc.getPlayer().getHand().toJson());
                            psPlayers.setString(3, id.toString());
                            if (psPlayers.executeUpdate() == 0) {
                                throw new SQLException("WTF IS GOING ON");
                            }
                        }
                    }
                    try (PreparedStatement setCurrentPlayer = conn.prepareStatement("UPDATE saved_games SET current_player=? WHERE id=?")) {
                        setCurrentPlayer.setString(2, id.toString());
                        setCurrentPlayer.setString(1, currentPlayer.getClientId().toString());
                        setCurrentPlayer.executeUpdate();
                    }
                }
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Called when a player draws a card and then syncs the state to all other players
     *
     * @param playerConnection The triggering connection
     * @throws IOException if the sync fails
     */
    public void drawCard(PlayerConnection playerConnection) throws IOException {
        playerConnection.getPlayer().addCard(drawPile.draw());
        setNextPlayer();
        Command msg = new Command("player_turn", "player.id:" + currentPlayer.getClientId());
        push(msg);
        sync(playerConnection.getClientId());
    }

    /**
     * called when player plays a card and then syncs the state to all players
     *
     * @param command The command that contains all the card data
     * @throws JsonProcessingException
     */
    public void playCard(Command command) throws IOException {
        String cardString = command.getArgs().get("card_played");
        Card card = new Card(cardString);
        if (card.isPowerCard()) {
            switch (card.getType()) {
                case BLOCK -> direction = direction * 2;
                case REVERSE -> direction = direction * -1;
                case DRAW_2 -> drawCard(2);
                case DRAW_4 -> drawCard(4);
                case NEW_COLOR -> {//Do nothing special
                }
            }
        }
        playPile.addCard(card);
        setNextPlayer();
        Command msg = new Command("player_turn", "player.id:" + currentPlayer.getClientId());
        push(msg);
        sync(command.getSender());
    }

    /**
     * Set the next active player
     */
    private void setNextPlayer() {
        int currentIndex = players.indexOf(currentPlayer);

        if (currentIndex + direction <= -1) {
            currentPlayer = players.get(players.size() - 1);
        } else if ((currentIndex + direction) >= players.size()) {
            currentPlayer = players.get(0);
        } else {
            currentPlayer = players.get(currentIndex + direction);
        }

        if (direction == 2) {
            direction = 1;
        } else if (direction == -2) {
            direction = -1;
        }
    }

    /**
     * Tell the next player in the turn order to draw cards
     *
     * @param i the number of cards to draw
     * @throws IOException
     */
    public void drawCard(int i) throws IOException {
        int currentIndex = players.indexOf(currentPlayer);
        PlayerConnection target;
        if (currentIndex + direction <= -1) {
            target = players.get(players.size() - 1);
        } else if ((currentIndex + direction) >= players.size()) {
            target = players.get(0);
        } else {
            target = players.get(currentIndex + direction);
        }
        target.giveCard(drawPile.draw(i));
    }

    public void setColor(String color, UUID sender) throws IOException {
        if (color.equals(Color.RED.toString())) {
            playPile.getTopCard().setColor(Color.RED);
        } else if (color.equals(Color.BLUE.toString())) {
            playPile.getTopCard().setColor(Color.BLUE);
        } else if (color.equals(Color.GREEN.toString())) {
            playPile.getTopCard().setColor(Color.GREEN);
        } else if (color.equals(Color.YELLOW.toString())) {
            playPile.getTopCard().setColor(Color.YELLOW);
        }

        sync(sender);
    }
}
