package game.views;

import javax.swing.*;
import java.awt.*;

/**
 * ColorPickerView is a window with colored buttons
 * @author Johanna Schüldt
 * @version 2021-03-05
 */

public class ColorPickerView extends JFrame {

    private JButton blue;
    private JButton green;
    private JButton red;
    private JButton yellow;
    /**
     * Constructs a new ColorPickerView
     */
    public ColorPickerView(){
        super("Choose color");
        setLocation(200,200);
        JPanel panel= new JPanel();
        blue = new JButton();
        green = new JButton();
        red = new JButton();
        yellow = new JButton();
        panel.setPreferredSize(new Dimension(120,120));
        blue.setBackground(Color.BLUE);
        green.setBackground(Color.GREEN);
        red.setBackground(Color.RED);
        yellow.setBackground(Color.YELLOW);
        panel.add(blue);
        panel.add(green);
        panel.add(red);
        panel.add(yellow);
        panel.setLayout(new GridLayout(2, 2));
        add(panel);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        pack();
    }
    /**
     * Returns the blue button
     */
    public JButton getBlue(){
        return blue;
    }

    /**
     * Returns the green button
     */
    public JButton getGreen(){
        return green;
    }

    /**
     * Returns the red button
     */
    public JButton getRed(){
        return red;
    }

    /**
     * Returns the yellow button
     */
    public JButton getYellow(){
        return yellow;
    }
}
