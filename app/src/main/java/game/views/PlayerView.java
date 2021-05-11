package game.views;

import models.Card;
import models.PlayerModel;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * @author Jonathan Sargent
 * @version 2021-03-05
 * View representing:
 *  1) The players Card
 *  2) Uno-button
 *  3) Sortbuttons
 */
public class PlayerView extends JPanel{

    private final PlayerModel playerModel;
    private JPanel innerCardPanel;
    private JButton unoButton;
    private JButton colorSortButton;
    private JButton valueSortButton;
    private LinkedList<JButton> handVisual;
    private Color standardButtonBackground;

    /**
     * @author Jonathan Sargent
     * @version 2021-03-05
     * @param playerModel
     */
    public PlayerView(PlayerModel playerModel){
        this.playerModel = playerModel;
        this.init();
        this.setSize(new Dimension(1200,200));
        this.setVisible(true);
    }

    /**
     * @author Jonathan Sargent
     * @version 2021-03-05
     * Initializes all the neccassary components of playerView
     */
    public void init(){

        this.handVisual = new LinkedList<>();
        // Create the innermost panel for holding cards..
        this.innerCardPanel = new JPanel(new GridLayout(1,30)); // for some reason cols dont matter here

        // Scrollbar stuff
        JScrollPane jScrollPane = new JScrollPane(innerCardPanel);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        // position & size probably ought to be based on the outer panel later
        jScrollPane.setBounds(0, 0, 600, 250);

        // add buttons to innerCardPanel
        updateHand();

        // panel that wraps scrollpane needs have layout set to null
        // since we want control over our layout this is just a wrapper for the
        // scrollbar so that it shows, we will then have another JPanel wrapping that
        //JPanel outerCardPanel = new JPanel(null);
        JPanel outerCardPanel = new JPanel(new GridLayout(1,1));

        outerCardPanel.setPreferredSize(new Dimension(600, 250));
        outerCardPanel.add(jScrollPane);

        // handle outer layout
        BorderLayout testLayout = new BorderLayout();
        this.setLayout(testLayout);

        this.valueSortButton = new JButton("valueSort");
        valueSortButton.setPreferredSize(new Dimension(140,75));
        valueSortButton.setFont(new Font("Comic Sans", Font.PLAIN, 18));
        this.colorSortButton = new JButton("colorSort");
        colorSortButton.setPreferredSize(new Dimension(140,75));
        colorSortButton.setFont(new Font("Comic Sans", Font.PLAIN, 18));
        unoButton = new JButton("UNO");
        unoButton.setPreferredSize(new Dimension(140,75));
        unoButton.setFont(new Font("Comic Sans", Font.BOLD, 18));

        //JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 5));
        buttonPanel.add(colorSortButton);

        buttonPanel.add(unoButton);
        this.standardButtonBackground = this.unoButton.getBackground();
        buttonPanel.add(valueSortButton);
        // collective size of buttons
        buttonPanel.setPreferredSize(new Dimension(600,85));

        // add components to outer
        this.add(buttonPanel, BorderLayout.CENTER);
        this.add(outerCardPanel, BorderLayout.NORTH);
    }

    /**
     * Returns Visual representation of player cards
     * @return LinkedList<JButton>
     */
    public LinkedList<JButton> getHandVisual() {
        return handVisual;
    }

    /**
     * Returs button used for sorting cards by color.
     * @return JButton
     */
    public JButton getColorSortButton() {
        return colorSortButton;
    }

    /**
     * Returs button used for sorting cards by value.
     * @return JButton
     */
    public JButton getValueSortButton() {
        return valueSortButton;
    }

    /**
     * Returs button used for calling UNO.
     * @return JButton
     */
    public JButton getUnoButton(){
        return this.unoButton;
    }

    /**
     * Updates the view of the hand depending on the playerModel
     */
    public void updateHand(){

        innerCardPanel.removeAll();
        handVisual = new LinkedList<>();

        for (Card card : playerModel.getHand()) {
            JButton jButton = new JButton(cardValueToText(card));
            jButton.setBackground(card.getColor());

            jButton.setPreferredSize(new Dimension(100, 150));
            this.handVisual.add(jButton);
            innerCardPanel.add(jButton);
            confText(jButton);
        }
    }

    /**
     * configures text size to be more appropriate, adjust color of font depending on background
     * @param jButton the JButton to be configured
     */
    private void confText(JButton jButton){
        Color background = jButton.getBackground();
        Font font = new Font("Comic Sans",Font.BOLD,36);
        jButton.setFont(font);

        if (background.equals(Color.BLUE) || background.equals(Color.BLACK)){
            jButton.setForeground(Color.WHITE);
        }else{
            jButton.setForeground(Color.BLACK);
        }
    }

    /**
     * repaints and changes value of cards from index to last element
     */
    public void rewriteView() {

        int index = 0;

        for (JButton button : handVisual) {
            Card card = this.playerModel.getHand().get(index);
            button.setText(cardValueToText(card));
            button.setBackground(card.getColor());
            confText(button);
            button.revalidate();
            index++;
        }

        this.revalidate();
        this.setVisible(true);
    }

    /**
     * Kanske flytta till cardklassen?
     * --
     * Converts the value of a card (0-9,+2,block,Reverse,+4) into a String,
     * @param card the card we want to extract information from
     * @return a String representing the value or powercardtype
     */
    private String cardValueToText(Card card){
        if(card.getType() == Card.PowerCardType.BLOCK){
            return "B";
        }else if(card.getType() == Card.PowerCardType.DRAW_2){
            return "+2";
        }else if(card.getType() == Card.PowerCardType.NEW_COLOR){
            return "Col";
        }else if(card.getType() == Card.PowerCardType.REVERSE){
            return "R";
        }else if(card.getType() == Card.PowerCardType.DRAW_4){
            return "+4";
        }else{
            return Integer.toString(card.getValue());
        }
    }

    /**
     * changes backgroundColor of UNO-Button depending on playerModel
     */
    public void updateUnoButtonVisual(){
        if(this.playerModel.isCalledUno()){
            this.unoButton.setBackground(Color.GREEN);
        }else{
            this.unoButton.setBackground(this.standardButtonBackground);
        }
    }
}