package game.views;

import javax.swing.*;
import java.awt.*;

/**
 *The DrawPileView is very simple, a button with the text UNO on it.
 * @author Lisa Löving
 * @version 2021-03-07
 */
public class DrawPileView extends JButton {
    public DrawPileView(){
        this.setBackground(Color.RED);
        setLayout(new GridBagLayout());
        Font f = new Font("Comic Sans", 1, 36);
        this.setFont(f);
        this.setText("UNO");
    }
}
