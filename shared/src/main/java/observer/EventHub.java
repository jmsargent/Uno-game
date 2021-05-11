package observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The main communication hub, an observer pattern
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class EventHub {
    /**
     * A map where each observer that has registered to a channel is stored
     */
    private HashMap<Channel, List<Observer>> observers = new HashMap<>();

    /**
     * Events for each channel, could probably call update with the Event object directly instead of temp storing it
     */
    private HashMap<Channel, ObserverEvent> events = new HashMap<>();

    /**
     * Add a event and notify all observers
     *
     * @param channel Channels to send to
     */
    public void addEvent(ObserverEvent observerEvent, Channel channel) {
        System.out.println("New event: " + observerEvent.getCommand().getName() + " on channel: " + channel);
        events.put(channel, observerEvent);
        Thread t = new Thread(() -> notifyObservers(channel));
        t.start();
    }


    /**
     * Subscribe an observer to a channel
     *
     * @param observer Observer to subscribe to channel
     * @param listenOn what channel to subscribe to
     */
    public void subscribe(Observer observer, Channel listenOn) {
        if (!observers.containsKey(listenOn)) {
            observers.put(listenOn, new ArrayList<>());
        }
        observers.get(listenOn).add(observer);
    }

    /**
     * Get the stored event for the channel
     *
     * @param channel The channel to check
     * @return the latest event on the channel
     */
    public ObserverEvent getEvent(Channel channel) {
        return events.get(channel);
    }

    /**
     * Notify all observers that is subscribed to a channel
     *
     * @param channel The channel to notify
     */
    public void notifyObservers(Channel channel) {
        if (observers.containsKey(channel) && !observers.get(channel).isEmpty())
            for (Observer observer : observers.get(channel)) {
                observer.update();
            }
    }
}