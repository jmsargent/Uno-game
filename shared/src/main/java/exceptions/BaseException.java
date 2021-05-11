package exceptions;

import interfaces.ToCommandArguments;
import network.CommandArguments;

/**
 * A baseline exception that our custom exceptions use
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public abstract class BaseException extends Exception implements ToCommandArguments {
    int code;
    Error err;

    protected BaseException() {
    }

    protected BaseException(String message) {
        super(message);
    }

    protected BaseException(String message, int code) {
        super(message);
        this.code = code;
    }

    protected BaseException(Error err) {
        super(err.getDescription());
        this.code = err.getCode();
        this.err = err;
    }

    public Error getError() {
        return err;
    }

    public int getCode() {
        return code;
    }

    public CommandArguments toCommandArguments() {
        return err.toCommandArguments();
    }
}
