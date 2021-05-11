package menu;

import exceptions.BaseException;
import network.CommandFactory;
import observer.Channel;
import observer.EventHub;
import observer.ObserverEvent;
import tools.ActiveGame;
import tools.CommunicationCaptain;
import tools.SavedGame;
import tools.Settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Felicia Berggren
 * @version 2021-03-07
 * Menu Controller handles the games main menu. The class also handles actions; buttonPressed and username entered
 */
public class MenuController implements ActionListener {
    private final Settings settings;
    private final EventHub eventHub;
    MenuView menuView;
    CommunicationCaptain communicationCaptain;
    GameButtonListener gameButtonListener = new GameButtonListener();

    /**
     * MenuController is the Controller for the games menu. This controller method creates
     * a connection to from the menu to the server and calls on setActionListener
     *
     * @param communicationCaptain
     * @param settings
     * @param eventHub
     */
    public MenuController(CommunicationCaptain communicationCaptain, Settings settings, EventHub eventHub) {
        menuView = new MenuView();
        this.settings = settings;
        this.communicationCaptain = communicationCaptain;
        this.eventHub = eventHub;
        setActionListener();
    }

    /**
     * Sets actionListener to all of the buttons in the menu and the users input(the username)
     */
    public void setActionListener() {
        menuView.getNewButton().addActionListener(this);
        menuView.getLoadButton().addActionListener(this);
        menuView.getJoinButton().addActionListener(this);
        menuView.getSettingsButton().addActionListener(this);
        menuView.getGoBackButton().addActionListener(this);
        menuView.getUserInput().addActionListener(this);
    }

    /**
     * Hide the menu from UI
     */
    public void hide() {
        menuView.setVisible(false);
    }

    public void show() {
        menuView.setVisible(true);
    }

    public void loadGame() {

    }

    private void personalSettings() {
    }

    private void startGame() {
        // Opens game on server?
    }

    private void joinGame(String gameId) {

    }

    /**
     * Calls on the relevant view method for the button being pressed or the view that sets the username
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuView.getUserInput()) {
            menuView.setUserName(menuView.getUserInput());
          /*  try {                                 //KOMMENTERA TILLBAKA
                communicationCaptain.newGame();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }*/
        } else if (e.getSource() == menuView.getGoBackButton()) {
            menuView.firstScreen();
        } else {
            menuView.secondScreen();
            if (e.getSource() == menuView.getNewButton()) {
                System.out.println("New game start");
                try {
                    communicationCaptain.newGame();
                    ObserverEvent observerEvent = new ObserverEvent(CommandFactory.startGame());
                    eventHub.addEvent(observerEvent, Channel.APP);
                    observerEvent.waitForFinish();
                    return;
                } catch (IOException | InterruptedException | BaseException ioException) {
                    ioException.printStackTrace();
                }
            }
            if (e.getSource() == menuView.getLoadButton()) {
                try {
                    for (SavedGame savedGame : communicationCaptain.getSavedGames()) {
                        //för varje loop. kalla på view med knappnamn savedGame.getGameName() till view.savedGamesButtons.
                        menuView.createSavedGamesButtons(savedGame.getGameName());
                        GameButton btn = menuView.loadGameView(savedGame);
                        btn.addActionListener(gameButtonListener);
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            if (e.getSource() == menuView.getJoinButton()) {
                try {
                    for (ActiveGame activeGame : communicationCaptain.getActiveGames()) {
                        menuView.createLoadGamesButtons(activeGame.getGameName());
                        GameButton btn = menuView.loadGameView(activeGame);
                        btn.addActionListener(gameButtonListener);
                    }
                } catch (IOException | InterruptedException ioException) {
                    ioException.printStackTrace();
                }
            }
            if (e.getSource() == menuView.getSettingsButton()) {
            }
            menuView.showSecondScreen();
        }
    }

    class GameButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            GameButton btn;
            if (e.getSource() instanceof GameButton) {
                btn = (GameButton) e.getSource();
            } else {
                return;
            }

            if (btn.getGameSession() instanceof ActiveGame)
                eventHub.addEvent(new ObserverEvent(CommandFactory.joinGame(settings.getPlayerName(), UUID.fromString(btn.getGameSession().getId()))), Channel.COMMUNICATION_CAPTAIN);
            else
                eventHub.addEvent(new ObserverEvent(CommandFactory.load(UUID.fromString(btn.getGameSession().getId()))), Channel.COMMUNICATION_CAPTAIN);
            ObserverEvent observerEvent = new ObserverEvent(CommandFactory.startGame());
            eventHub.addEvent(observerEvent, Channel.APP);
            try {
                menuView.firstScreen();
                observerEvent.waitForFinish();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            } catch (BaseException exception) {
                exception.printStackTrace();
            }
        }
    }
}
