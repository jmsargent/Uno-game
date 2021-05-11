package game;

import game.views.*;

import javax.swing.*;
import java.awt.*;

/**
 * Gameview is the View of the whole game, so all views implemented and working together as a game.
 * @author everyone
 * @version 2021-03-07
 */
public class GameView extends JFrame {

    private GameModel gameModel;
    private PlayerView playerView;
    private PlayPileView playPileView;
    private DrawPileView drawPileView;
    private ColorPickerView colorPickerView;
    private InGameMenuView inGameMenuView;
    private PlayerListView playerListView;
    private Boolean inProgress = false;

    public GameView(GameModel gameModel,PlayerView playerView, PlayPileView playPileView, DrawPileView drawPileView,
                    ColorPickerView colorPickerView, InGameMenuView inGameMenuView, PlayerListView playerListView){

        this.colorPickerView = colorPickerView;
        this.playerView = playerView;
        this.playPileView = playPileView;
        this.drawPileView = drawPileView;
        this.gameModel = gameModel;
        this.inGameMenuView = inGameMenuView;
        this.playerListView = playerListView;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(new Color(0,102,0));

        JPanel panel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new FlowLayout());

        //TEST FÖR BRA STORLEK PÅ DRAWVIEW
        //drawPileView.setSize(250,400);
        JPanel draw = new JPanel();
        draw.setPreferredSize(new Dimension(200,300));
        //this setLayout allows us to set a size to the button(drawPileView is a button)
        draw.setLayout(new GridLayout(1,1));
        draw.add(drawPileView);

        centerPanel.add(playPileView);
        centerPanel.add(draw);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(playerView, BorderLayout.SOUTH);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setSize(1200,100);

        northPanel.add(inGameMenuView, BorderLayout.NORTH);
        northPanel.add(playerListView, BorderLayout.CENTER);

        panel.add(northPanel, BorderLayout.NORTH);

        this.add(panel);
        //pack();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);

   }

    /**
     * If game is won, do this pop-up window.
     */
   public void gameIsWon(){
       JFrame f = new JFrame("Game is over!");
       f.setSize(200, 200);
       f.setLocation(150, 150);
       JPanel panel= new JPanel();
       panel.setPreferredSize(new Dimension(200,200));
       f.setBackground(Color.MAGENTA);
       Font font = new Font("Comic Sans", 1, 30);
       JLabel text = new JLabel("CONGRATS" + "sätt in player här" + "WON THE GAME!");
       text.setFont(font);
       panel.add(text);
       f.add(panel);
       f.setVisible(true);
   }
}
