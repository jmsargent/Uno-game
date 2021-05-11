package menu;

import interfaces.IGameSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedList;

/**
 * @author Felicia Berggren
 * @version 2021-03-07
 * Menu View extends JFrame to show the main and its components. The class also displays the the windows
 * that appears when a certain button is pressed in the main menu.
 */
public class MenuView extends JFrame {
    JPanel jpanel = (JPanel)this.getContentPane();
    JPanel gameButtons = new JPanel();
    private JButton newButton;
    private JButton loadButton;
    private JButton joinButton;
    private JButton settingsButton;
    private JFrame secondScreen;
    private JButton goBackButton;
    private JTextField userInput;
    private String userName;
    private Color purple = new Color(243, 192, 230);
    int buttonWidth = 80;
    int buttonHeight = 40;
    LinkedList savedGamesButtons = new LinkedList();
    LinkedList loadGamesButtons = new LinkedList();
    JPanel buttonPanel;
    String title;

    /**
     * MenuView is view for the games menu. This constructor creates the window for the
     * menu and calls on 2 methods to that creates all of the buttons in the menu.
     */
    MenuView(){

        title = "Uno";
        new JFrame(title);
        BorderLayout borderLayout = new BorderLayout(6,6);
        jpanel.setLayout(borderLayout); //To create gap between userName and the buttons

        createButtons();
        createSettingsButton();

        //Box to enter username
        userInput = new JTextField(10);
        jpanel.add(userInput, BorderLayout.NORTH);

        //Beauty purpose section
        jpanel.setBorder(new EmptyBorder(15,15,15,15));
        jpanel.setBackground(Color.PINK);
        getContentPane().setPreferredSize(new Dimension(700, 400));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);

        goBackButton = new JButton("Go back");
        goBackButton.setFont(new Font("Comic Sans", Font.PLAIN, 15));
    }

    /**
     * Methods that returns the games buttons and the textfield
     * @return JButton/userInput
     */
    public JButton getNewButton(){
        return newButton;
    }
    public JButton getLoadButton(){
        return loadButton;
    }
    public JButton getJoinButton(){
        return joinButton;
    }
    public JButton getSettingsButton(){
        return settingsButton;
    }
    public JButton getGoBackButton(){
        return goBackButton;
    }
    public JTextField getUserInput(){
        return userInput;
    }

    /**
     * Shows the menu Screen and hides other windows
     */
    public void firstScreen() {
        this.secondScreen.setVisible(false);
        this.setVisible(true);
    }

    /**
     * Hides the menu window and opens a new window with a "go back" button
     */
    public void secondScreen() {
        this.setVisible(false);

        this.secondScreen = new JFrame(title);
        this.secondScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.secondScreen.setPreferredSize(this.getSize());
        JPanel secondPanel = new JPanel();
        buttonPanel = new JPanel();
        secondPanel.setBackground(Color.PINK);
        BorderLayout borderLayout = new BorderLayout(6,6);
        secondPanel.setLayout(borderLayout);
        buttonPanel.setLayout(new GridLayout(17,1));
        goBackButton.setBackground(purple);
        JPanel east = new JPanel();
        east.setBackground(Color.PINK);
        east.add(goBackButton);
        east.setBorder(new EmptyBorder(getHeight()-2*buttonHeight,10,10,0));

        secondPanel.add(east, BorderLayout.EAST);

        this.secondScreen.add(secondPanel);
        secondPanel.setLocation(100,200);
        this.secondScreen.pack();
        this.secondScreen.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Shows the opened window and hides the menus window
     */
    public void showSecondScreen(){
        this.secondScreen.setVisible(true);
    }

    /**
     * Creates a button for the saved games every time its called (For load game)
     * @param buttonName
     */
    public void createSavedGamesButtons(String buttonName){
        savedGamesButtons.add(buttonName);
    }

    /**
     * Creates a button for the loaded games every time its called (For join game)
     * @param buttonName
     */
    public void createLoadGamesButtons(String buttonName) {
        loadGamesButtons.add(buttonName);
    }

    /**
     * Creates the buttons for them,enu
     */
    public void createButtons(){ // Creates 4 buttons in the menu; newButton, loadButton, joinButton, settingsButton
        gameButtons.setLayout(new GridLayout(3,1)); //To stack buttons vertically
        this.newButton = new JButton("New game");
        newButton.setFont(new Font("Comic Sans", Font.PLAIN, 18));
        this.loadButton = new JButton("Load game");
        loadButton.setFont(new Font("Comic Sans", Font.PLAIN, 18));
        this.joinButton = new JButton("Join game");
        joinButton.setFont(new Font("Comic Sans", Font.PLAIN, 18));
        this.settingsButton = new JButton("Settings");
        settingsButton.setFont(new Font("Comic Sans", Font.PLAIN, 18));

        newButton.setBackground(purple);
        loadButton.setBackground(purple);
        joinButton.setBackground(purple);
        settingsButton.setBackground(purple);

        gameButtons.add(newButton);
        gameButtons.add(loadButton);
        gameButtons.add(joinButton);

        jpanel.add(gameButtons, BorderLayout.WEST);
    }

    /**
     * Creates the settings button
     */
    public void createSettingsButton(){ //To create the button for the settings. Is called on in the constructor
        JPanel east = new JPanel();
        east.add(settingsButton);
        east.setBorder(new EmptyBorder(160,10,10,0));
        east.setBackground(Color.PINK);
        jpanel.add(east, BorderLayout.EAST);
    }

    /**
     * Takes the input from the user and stores in the instance variable "username"
     * @param userInput
     */
    public void setUserName(JTextField userInput) {
        userName = userInput.getText();
    }

    /**
     * Takes the names of the buttons as input and adds them to the new opened screen.
     * @param game an object containing the info about the game
     */
    public GameButton loadGameView(IGameSession game) {
        GameButton j = new GameButton(game);
        buttonPanel.add(j);
        title = "Load game";
        secondScreen.add(buttonPanel);
        return j;
    }

}
