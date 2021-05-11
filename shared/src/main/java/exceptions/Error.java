package exceptions;

import interfaces.ToCommandArguments;
import network.Command;
import network.CommandArguments;

/**
 * Some common error values
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public enum Error implements ToCommandArguments {
    INVALID_COMMAND(1, "INVALID_COMMAND", "No such command"),
    NO_SUCH_GAME_SESSION(2, "NO_SUCH_GAME_SESSION", "No session with that id"),
    INVALID_CARD(3, "INVALID_CARD", "Invalid card played"),
    FAILED_TO_CONNECT(4, "FAILED_TO_CONNECT", "failed to connect to the game"),
    EVENT_TIMEOUT(5, "EVENT_TIMEOUT", "Event timed out"),
    EVENT_NOT_FINISHED(6, "EVENT_NOT_FINISHED", "event has not been finished yet");
    private final int code;
    private final String description;
    private final String name;
    private String value;

    private Error(int code, String name, String description) {
        this.code = code;
        this.description = description;
        this.name = name;
    }

    public Command toCommand() {
        if (this.value != null && !this.value.equals("")) {
            return new Command(this, value);
        }
        return new Command(this);
    }

    public Error withValue(String value) {
        this.value = value;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return code + ": " + description;

    }

    @Override
    public CommandArguments toCommandArguments() {
        CommandArguments args = new CommandArguments();
        args.put("error.type", name);
        args.put("error.code", getCode() + "");
        args.put("error.description", getDescription());
        args.put("values", value);
        return args;
    }
}
