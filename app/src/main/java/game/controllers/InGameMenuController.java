package game.controllers;

import exceptions.BaseException;
import game.views.InGameMenuView;
import network.Command;
import network.CommandFactory;
import observer.Channel;
import observer.EventHub;
import observer.ObserverEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author Felicia Berggren
 * @version 2021-03-07
 * InGameMenu Controller handles the games menu inside of the menu. This consists of a button to start the game which
 * turns in to button which saves the game once pressed. It also conists och a go back button which returns the player
 * to the main menu.
 */
public class InGameMenuController implements ActionListener {
    //In game menu buttons for Start/Save game, Settings etc
    InGameMenuView inGameMenuView;
    EventHub eventHub;

    /**
     * The constructor creates the 2 buttons for the ingamemenuview. It also calls on the method to set the
     * actionlisteners
     * @param eventHub
     */
    public InGameMenuController(EventHub eventHub) {
        this.eventHub = eventHub;
        inGameMenuView = new InGameMenuView();
        setActionListener();
    }

    /**
     * Adds the actionlistener to the save game and go back button
     */
    private void setActionListener() {
        inGameMenuView.getSaveButton().addActionListener(this);
        inGameMenuView.getgoBackButton().addActionListener(this);
    }

    /**
     * @return inGameMenuView
     */
    public InGameMenuView getView() {
        return inGameMenuView;
    }

    /**
     * if start is pressed game starts and Start button "start game" changes to "save game"
     */
    public void startPressed() {

    }

    /**
     * If the save button is pressed, fetch the game state and write it to a file
     */
    public void savePressed() {
        Command command = CommandFactory.save();
        ObserverEvent observerEvent = new ObserverEvent(command);
        eventHub.addEvent(observerEvent, Channel.COMMUNICATION_CAPTAIN);
        try {
            observerEvent.waitForFinish(2); //efter 2 sek stängs spelet om inget exception sker
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BaseException e) {
            //Hantera om sparning misslyckades
            //stäng inte ner spelet. ge meddelande om att sparning misslyckades
            inGameMenuView.saveFailedPopUp();
        }
    }

    /**
     * When the startbutton has been pressed this method is called. It turns the start game button to a save game button
     */
    public void gameStarted() {
        inGameMenuView.toggleSaveButton();
    }

    /**
     * Handles the actionevents. It calls on methods to either start tha game, save the game or to go back to main menu
     * depending on which method is called.
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == inGameMenuView.getSaveButton()) {
            if (inGameMenuView.getSaveButton().getText().equals("Start game")) {
                startGame();
            } else {
                savePressed();
            }
        }
        if (e.getSource() == inGameMenuView.getgoBackButton()) {
            ObserverEvent observerEvent = new ObserverEvent(CommandFactory.disconnect());
            eventHub.addEvent(observerEvent, Channel.APP);
        }
    }


    /**
     * Tells the server to start the game
     */
    private void startGame() {
        eventHub.addEvent(new ObserverEvent(CommandFactory.startGame()), Channel.COMMUNICATION_CAPTAIN);
    }
}
