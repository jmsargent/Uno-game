package game.controllers;

import game.views.DrawPileView;
import models.Card;
import models.DrawPile;
import network.Command;
import network.CommandFactory;
import observer.Channel;
import observer.EventHub;
import observer.Observer;
import observer.ObserverEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * DrawPile handles the pile from which the players draw new cards from. When a player presses the drawPile
 * an ObserverEvent reacts and sends the card on top of the pile to the player, through the channel PLAYER.
 * @author Lisa Löving
 * @version 2021-03-07
 */

public class DrawPileController extends Observer {
    private DrawPile drawPile;
    private DrawPileView drawPileView;
    private EventHub eventHub;
    private Boolean player_turn = false;

    public DrawPileController(EventHub eventHub) {
        drawPile = new DrawPile();
        drawPileView = new DrawPileView();
        this.eventHub = eventHub;
        //listening to everything sent to the DRAW_PILE Channel
        eventHub.subscribe(this, Channel.DRAW_PILE);
        drawPileView.addActionListener(new ActionListener() {

            /**
             * if the draw-pile is clicked at, draw a card, and send it to player through the channel.
             * @param e is an ActionEvent
             */
            public void actionPerformed(ActionEvent e) {
                if (player_turn) {
                    //drawPile.draw() returns the drawn card(see below)
                    Command command = CommandFactory.drawCard(drawPile.draw());
                    ObserverEvent event = new ObserverEvent(command);
                    ObserverEvent toCC = new ObserverEvent(command);
                    // add event to the channel owned by the receiver
                    eventHub.addEvent(event, Channel.PLAYER);
                    eventHub.addEvent(toCC, Channel.COMMUNICATION_CAPTAIN);
                }
            }
        });
    }

    /**
     * @return View for this DrawPile
     */
    public DrawPileView getDrawPileView() {
        return this.drawPileView;
    }

    /**
     * @return Model for this DrawPile
     */
    public DrawPile getDrawPileModel() {
        return this.drawPile;
    }

    /**
     * sync method for the class.
     * @param command is the incoming command
     */
    private void sync(Command command) {
        try {
            DrawPile drawPile = new DrawPile().fromJSON(command.getArgs().get("drawPile"));
            System.out.println("updating draw pile");
            this.drawPile.clear();
            this.drawPile.addAll(drawPile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive updates from the EventHub as an observer
     */
    @Override
    public void update() {
        ObserverEvent event = eventHub.getEvent(Channel.DRAW_PILE);
        switch (event.getCommand().getName()) {
            case "sync" -> sync(event.getCommand());
            case "save_card" -> { //Method used for adding already played cards to the drawpile, to later become the new (shuffeled) drawpile
                String cardString = event.getCommand().getArgs().get("card");
                drawPile.addPlayedCard(new Card(cardString));
            }
            case "turn_status" ->player_turn = Boolean.valueOf(event.getCommand().getArgs().get("your_turn"));
        }
    }
}


