package network;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.BaseException;
import exceptions.Error;
import interfaces.ToCommandArguments;
import interfaces.ToJSON;
import java.io.IOException;
import java.util.UUID;

/**
 * The main form of communication within the entire project, the command class carries all the data arguments that both
 * the events and server use on both sides.
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class Command implements ToJSON {
    private String name; //The name of the command to run
    private CommandArguments args; //Arguments for the command, will create a document explaining all available commands
    private UUID sender;

    /**
     * Create a new command without any arguments
     *
     * @param name The name of the command
     */
    public Command(String name) {
        this.name = name;
        this.args = new CommandArguments();
    }

    /**
     * Convert an Error to a command
     *
     * @param err The error to convert
     */
    public Command(Error err) {
        this.name = "error";
        this.args = err.toCommandArguments();
    }

    /**
     * Create a new command with supplied arguments
     *
     * @param name The name of the command
     * @param args The arguments
     */
    public Command(String name, CommandArguments args) {
        this.name = name;
        this.args = args;
    }

    /**
     * Convert an error into a command with a supplied value appended
     *
     * @param err   The error to convert
     * @param value The appended value
     */
    public Command(Error err, String value) {
        this.name = "error";
        this.args = err.withValue(value).toCommandArguments();
    }

    /**
     * Convert an Error to a command
     *
     * @param exception The error to convert
     */
    public Command(BaseException exception) {
        this(exception.getError());
    }

    /**
     * @param name The command name
     * @param model The model to turn into argument format
     * @throws IOException if the conversion fails
     */
    public Command(String name, ToCommandArguments model) throws IOException {
        this.name = name;
        this.args = model.toCommandArguments();
    }

    /**
     * @param name The command name
     * @param pairs The key:value string pairs to turn into CommandArguments
     */
    public Command(String name, String... pairs) {
        this.name = name;
        this.args = new CommandArguments(pairs);
    }

    /**
     * Should only be used when calling fromJSON()
     */
    public Command() {}

    /**
     * @return The name of the command
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The list of arguments
     */
    public CommandArguments getArgs() {
        return args;
    }

    /**
     * Set the name of the command to send to the server
     *
     * @param name the name of the command
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set all the arguments
     *
     * @param args all the arguments for the command
     */
    public void setArgs(CommandArguments args) {
        this.args = args;
    }

    /**
     * Convert a JSON string into this object type
     *
     * @param json the json string
     * @return a new Command object
     * @throws IOException if something goes wrong while converting from JSON this error will be thrown
     */
    @JsonCreator
    public Command fromJSON(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, Command.class);
    }

    @Override
    public String toString() {
        try {
            return toJson();
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    /**
     * @return the sender ID
     */
    public UUID getSender() {
        return sender;
    }

    /**
     * @param sender The id of the sender
     */
    public void setSender(UUID sender) {
        this.sender = sender;
    }
}
