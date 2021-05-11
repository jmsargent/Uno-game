package observer;

import exceptions.BaseException;
import exceptions.EventTimeoutException;
import network.Command;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The event class that is passed from the EventHub, the class has fields for response and exceptions whilst also
 * having methods to wait for the result.
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class ObserverEvent {
    private LinkedBlockingQueue<BaseException> exception = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Boolean> finished = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Command> response = new LinkedBlockingQueue<>();
    private Command command;

    /**
     * Create a new Event to be handled via the EventHub
     * @param command The command to execute
     */
    public ObserverEvent(Command command) {
        this.command = command;
    }

    /**
     * Marked this event as finished and set an exception (Can be null)
     * @param exception the exception to set
     */
    public void finished(BaseException exception) {
        if (this.finished.isEmpty()) {
            if (exception != null) {
                this.exception.add(exception);
            }
            this.finished.add(true);
        }
    }

    /**
     * Marked this event as finished with response
     * @param response the response to send back
     */
    public void finished(Command response) {
        if (this.finished.isEmpty()) {
            if (response != null) {
                this.response.add(response);
            }

            this.finished.add(true);
        }
    }

    /**
     * Marked this event as finished without exception
     */
    public void finished() {
        if (this.finished.isEmpty()) {
            this.finished.add(true);
        }
    }

    /**
     * Wait for the event to be marked as finished, with a 60 seconds timeout
     * @throws InterruptedException
     */
    public void waitForFinish() throws InterruptedException, BaseException {
        waitForFinish(60);
    }

    /**
     * @param timeout the time in seconds that the function should wait at the most
     * @throws InterruptedException
     */
    public void waitForFinish(int timeout) throws InterruptedException, BaseException {
        for (int i = 0; i < timeout; i++) {
            if (finished.poll(1, TimeUnit.SECONDS) != null) {
                if (exception.peek() != null) {
                    throw getException();
                }
                return;
            }
        }
        throw new EventTimeoutException();
    }

    public BaseException getException() {
        return this.exception.peek();
    }

    /**
     * @return The command to execute
     */
    public Command getCommand() {
        return command;
    }

    /**
     * @return The command to execute
     */
    public Command getResponse() {
        return response.poll();
    }
}
