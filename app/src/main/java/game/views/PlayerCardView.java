package game.views;

import models.PlayerModel;

import javax.swing.*;
import java.awt.*;

/**
 * The player information view of a single player
 * @author Johanna Schüldt
 * @version 2021-03-06
 */

public class PlayerCardView extends JPanel {
    private PlayerModel player;
    private JLabel playerName;
    private JLabel numberOfCards;
    private JLabel uno;
    private JPanel unoPanel;

    /**
     * Constructs a new PlayerCardView
     * @param player
     */
    public PlayerCardView(PlayerModel player) {

        this.player = player;
        setPreferredSize(new Dimension(190,150));
        setBackground(Color.WHITE);
        playerName = new JLabel("  Player: " + player.getName());
        this.add(playerName);
        playerName.setFont(new Font("Comic Sans", Font.BOLD, 18));
        numberOfCards = new JLabel("  Cards: " + player.numberOfCards());
        numberOfCards.setFont(new Font("Comic Sans", Font.BOLD, 18));
        this.add(numberOfCards);
        unoPanel = new JPanel();
        uno = new JLabel("  UNO");
        unoPanel.add(uno);
        this.add(unoPanel);
        uno.setFont(new Font("Comic Sans", Font.BOLD, 18));
        unoPanel.setLayout(new GridLayout());
        unoPanel.setBackground(Color.lightGray);
        setLayout(new GridLayout(3, 1));
    }

    /**
     * Updates the number cards on a player's hand
     */
    public void updateCards(PlayerModel player){
        numberOfCards.setText("  Cards: " + player.numberOfCards());
    }

    /**
     * Updates the uno status of a player
     */
    public void updateUno(PlayerModel player){
        if(player.isCalledUno()){
            unoPanel.setBackground(Color.green);
        }
        else {
            unoPanel.setBackground(Color.lightGray);
        }
    }

    /**
     * Updates player status
     */
    public void updatePlayerTurn(PlayerModel player){
        if(player.isPlayersTurn()){
            setBackground(Color.MAGENTA);
        }
        else {
            setBackground(Color.WHITE);

        }

    }

}