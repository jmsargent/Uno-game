package observer;

/**
 * This is an abstract class to force the Class that extends this to have the proper methods and variables
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public abstract class Observer {
    protected EventHub eventHub;

    public abstract void update();
}