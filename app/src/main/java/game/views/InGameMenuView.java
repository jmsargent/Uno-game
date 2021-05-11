package game.views;

import javax.swing.*;
import java.awt.*;

/**
 * @author Felicia Berggren
 * @version 2021-03-07
 * InGameMenuView extends JPanel that contains the button for the start/save game button and the go back button
 */
public class InGameMenuView extends JPanel{
    //In game menu view with Save game, settings etc.
    private JButton saveButton;
    private JFrame settingsScreen;
    private JButton goBackButton;
    int buttonHeight = 40;

    /**
     * Creates a gridLayout with the 2 buttons next to each other and shows them
     */
    public InGameMenuView() {
        super(new GridLayout(1, 2, 10, 10));
        saveButton = new JButton("Start game");
        saveButton.setPreferredSize(new Dimension(100,20));
        saveButton.setFont(new Font("Comic Sans", Font.PLAIN, 15));

        add(saveButton);

        goBackButton = new JButton("Go back");
        goBackButton.setFont(new Font("Comic Sans", Font.PLAIN, 15));

        add(goBackButton);

        this.setSize(300, 150);
        this.setVisible(true);
    }

    /**
     * @return saveButton
     */
    public JButton getSaveButton(){
        return saveButton;
    }

    /**
     * @return goBackButton
     */
    public JButton getgoBackButton(){
        return goBackButton;
    }

    /**
     * Shows a window with a fail message if the save was unsuccessful
     */
    public void saveFailedPopUp() {
        JFrame saveFailed = new JFrame();
        JLabel label = new JLabel("Save failed");
        saveFailed.add(label);
        saveFailed.setVisible(true);
        this.settingsScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Sets the text on the start game button to save game instead
     */
    public void toggleSaveButton() {
        saveButton.setText("Save Game");
        saveButton.repaint();
    }

    public static void main(String[] args){
     new InGameMenuView();
 }

}
