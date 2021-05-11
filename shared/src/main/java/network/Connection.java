package network;

import java.io.*;
import java.net.Socket;

/**
 * A helper class for the sockets
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class Connection {
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final Socket socket;

    /**
     * Create a connection from a socket
     *
     * @param s the socket
     * @throws IOException if we fail to open any of the streams
     */
    public Connection(Socket s) throws IOException {
        socket = s;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Create a connection from a host and port combination
     *
     * @param host the hostname or ip
     * @param port the port to connect to
     * @throws IOException if we fail to open any of the streams
     */
    public Connection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * write raw string to the socket, not recommended
     *
     * @param msg the raw string to send
     * @throws IOException thrown if the write failed
     */
    public void write(String msg) throws IOException {
        writer.write(msg + "\r\n");
        writer.flush();
    }

    /**
     * Write multiple raw strings to the socket, not recommended
     *
     * @param msgs the raw strings to send
     * @throws IOException thrown if the write failed
     */
    public void write(String[] msgs) throws IOException {
        for (String msg : msgs) {
            writer.write(msg + "\r\n");
        }
        writer.flush();
    }

    /**
     * Send a command to the socket, the preferred way of communicating with the clients
     *
     * @param command the command to send
     * @throws IOException thrown if the write failed
     */
    public void write(Command command) throws IOException {
        write(command.toJson());
    }

    /**
     * Send an error to the socket, the preferred way of communicating with the clients
     *
     * @param err
     * @throws IOException
     */
    public void write(exceptions.Error err) throws IOException {
        write(new Command(err));
    }

    public Boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    /**
     * Read a command from the socket
     * @return a Command object
     * @throws IOException thrown if we fail to read from the socket
     */
    public Command read() throws IOException {
        String msg = reader.readLine();
        System.out.println(msg);
        return new Command().fromJSON(msg);
    }

    /**
     * Like the read above, only that this blocks until it receives a message
     * @return a Command object
     * @throws IOException thrown if we fail to read from the socket
     * @throws InterruptedException if the thread was interrupted
     */
    public Command readBlocking() throws IOException, InterruptedException {
        String msg;
        while ((msg = reader.readLine()) == null) {
            Thread.sleep(100);
        }
        return new Command().fromJSON(msg);
    }
}
