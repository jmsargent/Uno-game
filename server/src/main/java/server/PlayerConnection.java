package server;

import exceptions.Error;
import interfaces.ToJSON;
import models.Card;
import models.CardPile;
import models.PlayerModel;
import network.Command;
import network.CommandFactory;
import network.Connection;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

/**
 * The connection thread class for the incoming player connections
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class PlayerConnection implements Runnable, ToJSON {
    private Connection connection;
    private PlayerModel player;
    private Thread thread;
    private UUID clientId;
    private Session session;

    public PlayerConnection(Socket connection) throws IOException {
        this.connection = new Connection(connection);
        this.player = new PlayerModel("Unnamed", new CardPile(), UUID.randomUUID(), false);
    }

    public PlayerConnection(Connection connection, String playerName, UUID clientId) {
        this.connection = connection;
        this.player = new PlayerModel(playerName, new CardPile(), clientId, false);
        this.clientId = clientId;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public PlayerModel getPlayer() {
        return player;
    }

    public void giveCard(Card card) throws IOException {
        player.addCard(card);
        Command command = CommandFactory.drawnCards(card);
        connection.write(command);
    }

    public void giveCard(Card... cards) throws IOException {
        for (Card card : cards) {
            player.addCard(card);
        }
        Command command = CommandFactory.drawnCards(cards);
        connection.write(command);
    }

    public void setPlayer(PlayerModel player) {
        this.player = player;
    }

    public void ping() throws IOException {
        Command command = new Command("Ping");
        connection.write(command);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Command command = connection.readBlocking();
                switch (command.getName()) {
                    case "change_color_response":
                        session.setColor(command.getArgs().get("color"), clientId);
                        break;
                    case "play_card":
                        session.playCard(command);
                        player.getHand().pop();
                        break;
                    case "draw_card":
                        session.drawCard(this);
                        break;
                    case "save":
                        session.save();
                        break;
                    case "call_uno":
                        break;
                    case "start_game":
                        session.setInProgress(true);
                        session.push(CommandFactory.gameStarted());
                        session.setup();
                        break;
                    case "ping":
                        connection.write(CommandFactory.ping());
                        break;
                    case "error":
                        System.out.println(command.toJson());
                        break;
                    case "game_started":
                        break;
                    default:
                        connection.write(Error.INVALID_COMMAND.withValue(command.toJson()));
                        break;
                }
            } catch (java.net.SocketException e) {
                session.playerDisconnectedEvent(this);
                System.out.println("Player Connection, " + player.getName() + ": has been interrupted");
                thread.interrupt();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
