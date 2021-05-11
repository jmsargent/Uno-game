package interfaces;

import network.CommandArguments;

import java.io.IOException;

/**
 * The ToCommandArguments method is used by some commands to generate the arguments to send
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public interface ToCommandArguments {
    CommandArguments toCommandArguments() throws IOException;
}
