package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import exceptions.Error;
import exceptions.InvalidGameSessionException;
import network.Command;
import network.CommandArguments;
import network.CommandFactory;
import network.Connection;
import tools.ActiveGame;
import tools.SavedGame;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Makes sure actions taken by players are in-line with the game rules
 * Universally updates the game state when an action occurs
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class Server implements Runnable {
    private final int port;
    private Database database;
    LinkedHashMap<UUID, Session> sessions = new LinkedHashMap<>();
    LinkedHashMap<UUID, LinkedBlockingQueue<PlayerConnection>> queues = new LinkedHashMap<>();
    ServerSocket serverSocket;

    /**
     * Initialize the server thread object and database
     *
     * @param port The port to listen on
     */
    public Server(int port) {
        if (port > 1000 && port <= 65525) {
            this.port = port;
        } else
            throw new IllegalArgumentException("Port has to be in the span 1000-65525");
        try {
            database = new Database();
            database.initDatabase();
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
            System.exit(1);
        }
    }

    public Server() {
        this(9991);
    }

    /**
     * @param args you can supply a port as first argument to the program, otherwise it will use the default port of 9991
     */
    public static void main(String[] args) {
        Thread thread;
        Server server;
        if (args != null && args.length > 0) {
            server = new Server(Integer.parseInt(args[0]));
        } else {
            server = new Server();
        }
        thread = new Thread(server);
        thread.start();
    }

    /**
     * Start a game session based on saved data and add the requester to the connection queue of that session
     *
     * @param c       The connection of the "host"
     * @param command the data carrier
     */
    public void loadGame(Connection c, Command command) throws InvalidGameSessionException {
        LinkedBlockingQueue<PlayerConnection> queue = new LinkedBlockingQueue<>();
        String gameID = command.getArgs().get("game.id");
        Session session;
        try (java.sql.Connection conn = database.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM saved_games WHERE id=?")) {
                ps.setString(1, gameID);
                CommandArguments args = command.getArgs();
                PlayerConnection pc = new PlayerConnection(c, args.get("player.name"), command.getSender());
                session = new Session(ps.executeQuery(), queue);
                queue.add(pc);
                sessions.put(session.getId(), session);
                queues.put(session.getId(), queue);
                Thread thread = new Thread(session);
                session.setThread(thread);
                thread.start();
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Start a new game session and add the requester to the connection queue of that session
     *
     * @param c       The connection of the "host"
     * @param command the data carrier
     */
    public void newGame(Connection c, Command command) {
        LinkedBlockingQueue<PlayerConnection> queue = new LinkedBlockingQueue<>();
        Session session = new Session(queue);
        CommandArguments args = command.getArgs();
        PlayerConnection pc = new PlayerConnection(c, args.get("player.name"), command.getSender());
        queue.add(pc);
        sessions.put(session.getId(), session);
        queues.put(session.getId(), queue);
        Thread thread = new Thread(session);
        session.setThread(thread);
        thread.start();
    }

    /**
     * Add a connection to the connection queue of an ongoing game session
     *
     * @param c       The connection
     * @param command the data carrier
     * @throws InvalidGameSessionException if theres no such game session return an error
     */
    public void joinGame(Connection c, Command command) throws InvalidGameSessionException {
        CommandArguments args = command.getArgs();
        PlayerConnection pc = new PlayerConnection(c, args.get("player.name"), command.getSender());
        UUID gameID = UUID.fromString(args.get("game.id"));
        if (!gameExists(gameID)) {
            throw new InvalidGameSessionException();
        }
        queues.get(gameID).add(pc);
    }

    /**
     * Check if the game session and game queue exists
     *
     * @param gameID The ID of the game session
     * @return if the session and queue exists
     */
    private boolean gameExists(UUID gameID) {
        return (queues.containsKey(gameID) && sessions.containsKey(gameID));
    }

    @Override
    public void run() {
        try {
            if (port > 0) {
                serverSocket = new ServerSocket(port);
            }
            System.out.println("Server: Running ");
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Server: Waiting for connection");
                Connection connection = new Connection(serverSocket.accept());
                System.out.println("Server: New Connection");
                new Thread(new Handler(connection)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compose a list of active games and return to the requester
     *
     * @param connection The connection to send the response to
     */
    private void getActiveGames(Connection connection) {
        CommandArguments ca = new CommandArguments();
        int i = 0;
        for (Map.Entry<UUID, Session> entry : sessions.entrySet()) {
            ActiveGame ag = new ActiveGame(entry.getKey(), entry.getValue().getName(), entry.getValue().getPlayers(), entry.getValue().isInProgress());
            try {
                ca.put("game." + i, ag.toJson());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            i++;
        }

        Command command = new Command("active_games", ca);
        try {
            connection.write(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compose a list of saved games and return to the requester
     *
     * @param connection The connection to send the response to
     */
    private void getSavedGames(Connection connection, String clientID) {
        CommandArguments ca = new CommandArguments();
        try (java.sql.Connection conn = database.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT sg.id, sgpc.num FROM saved_games sg\n" +
                    "LEFT JOIN saved_game_players sgp on sg.id = sgp.game_id\n" +
                    "LEFT JOIN (SELECT game_id, count() as num FROM saved_game_players GROUP BY game_id) sgpc on sgpc.game_id = sg.id\n" +
                    "WHERE sgp.player_id = ?\n" +
                    "group by sg.id")) {
                ps.setString(1, clientID);
                ResultSet rs = ps.executeQuery();
                int i = 0;
                while (rs.next()) {
                    SavedGame sg = new SavedGame(UUID.fromString(rs.getString("id")), "Saved Game", null, null);
                    ca.put("game." + i, sg.toJson());
                    i++;
                }
            }
        } catch (SQLException | JsonProcessingException throwables) {
            throwables.printStackTrace();
        }
        Command command = new Command("saved_games", ca);
        try {
            connection.write(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Handler implements Runnable {

        Connection connection;

        public Handler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Command command = connection.readBlocking();

                    switch (command.getName().toLowerCase()) {
                        case "ping":
                            connection.write(CommandFactory.ping());
                            break;
                        case "join_game":
                            try {
                                joinGame(connection, command);
                                Thread.currentThread().interrupt();
                            } catch (InvalidGameSessionException e) {
                                connection.write(e.getError().withValue(command.toJson()));
                            }
                            break;
                        case "load_game":
                            try {
                                loadGame(connection, command);
                                Thread.currentThread().interrupt();
                            } catch (InvalidGameSessionException e) {
                                connection.write(e.getError().withValue(command.toJson()));
                            }
                            break;
                        case "new_game":
                            newGame(connection, command);
                            Thread.currentThread().interrupt();
                            break;
                        case "get_active_games":
                            getActiveGames(connection);
                            break;
                        case "get_saved_games":
                            getSavedGames(connection, command.getSender().toString());
                            break;
                        default:
                            connection.write(Error.INVALID_COMMAND.withValue(command.toJson()));
                            break;
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Thread.currentThread().interrupt();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
