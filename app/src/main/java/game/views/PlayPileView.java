package game.views;


import models.Card;
import models.PlayPile;

import javax.swing.*;
import java.awt.*;

/**
 * PlayPileView handles the graphics for the PlayPile. It updates (sort of a repaint) when a new card is added to the pile.
 * @author Lisa Löving
 * @version 2021-03-07
 */
public class PlayPileView extends JPanel  {
    private PlayPile model;
    private Card topCard;
    JLabel text = new JLabel("");

    public PlayPileView(PlayPile model){
        this.model=model;
        paintTopCard();
        setLayout(new GridBagLayout());
        this.add(text);
    }

    /**
     * This method can be called upon from outside of this class to paint the latest top card.
     */
    public void updateView(){
        paintTopCard();
    }

    /**
     * This method paints the top card in the PlayPile. It sets the correct background color and
     * writes out the correct text on the card as well, value or type.
     */
    private void paintTopCard(){
        //get the (new) top card from model (OBS! need to update model(PlayPile.addCard) before calling on this view!)
        topCard=model.getTopCard();
        setPreferredSize(new Dimension(200,300));
        Color c = topCard.getColor();
        setBackground(c);
        int value = topCard.getValue();
        text.setFont(new Font("Comic Sans", 1, 36));
        //Set text color white if dark cards (blue/Black)
        if(c.equals(Color.BLACK) || c.equals(Color.BLUE)){
            text.setForeground(Color.WHITE);
        }else{
            text.setForeground(Color.BLACK);
        }
        if(topCard.isPowerCard()){
           Card.PowerCardType s = topCard.getType();
           String s1;
           if(s.equals(Card.PowerCardType.NEW_COLOR)){ s1 = "COLOR";}
           else if(s.equals(Card.PowerCardType.REVERSE) || s.equals(Card.PowerCardType.BLOCK)){s1=""+s;}
           else if(s.equals(Card.PowerCardType.DRAW_2)){s1= "+2";}
           else if(s.equals(Card.PowerCardType.DRAW_4)){s1= "+4";}
           else{s1="";}
            text.setText(s1);
        }
        else{
            text.setText(String.valueOf(value));
        }
        this.revalidate();
    }
}
