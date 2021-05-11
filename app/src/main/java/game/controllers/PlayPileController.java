package game.controllers;

import exceptions.BaseException;
import exceptions.InvalidCardException;
import game.views.PlayPileView;
import models.Card;
import models.PlayPile;
import network.Command;
import network.CommandFactory;
import observer.Channel;
import observer.EventHub;
import observer.Observer;
import observer.ObserverEvent;

import java.awt.*;
import java.io.IOException;

/**
 * PlayerPile is the pile where the player's latest played card is shown on top. This is the same pile for all the players
 * in the game. This controller method updates its model and view counterpart, aka updating the card shown on top.
 *
 * @author Lisa Löving
 * @version 2021-03-07
 */
public class PlayPileController extends Observer {
    private PlayPileView view;
    private PlayPile model;
    private EventHub eventHub;
    Boolean firstRound;

    public PlayPileController(EventHub eventHub) {
        model = new PlayPile();
        model.initialize();
        view = new PlayPileView(model);
        this.eventHub = eventHub;
        //listening to everything sent to the PLAY_PILE Channel
        eventHub.subscribe(this, Channel.PLAY_PILE);
        this.firstRound = true;
    }

    /**
     * The last played card shall become the card resting on the top (the visible one), also handles the logic for
     * PowerCards etc. inside the called method addCard.
     *
     * @param card is the new card we want to update the PlayPile with.
     */
    public void updateTopCard(Card card) {
        model.addCard(card);
        dealWithSpecialCards();
        view.updateView();
    }

    /**
     * The update method in PlayPileController starts running when a player (from PlayerController) press a card to play.
     * This method gets a message about it and adds the card to the played pile if you can play the card. If not, then
     * it throws an exception.
     */
    @Override
    public void update() {
        //Here we want to get the card that is being played, and then call on the method updateTopCard
        ObserverEvent event = eventHub.getEvent(Channel.PLAY_PILE);
        Command command = event.getCommand();
        String cardString;
        switch (command.getName()) {
            case "play_card":
                cardString = command.getArgs().get("card_played");
                if (model.isValidPlay(new Card(cardString))) {
                    updateTopCard(new Card(cardString));
                    event.finished();
                    //we also want to tell the drawpile to add the old top card to the drawpile for later reuse
                    if (!firstRound) {
                        ObserverEvent event2 = new ObserverEvent(CommandFactory.saveCard(model.getPlayedCard()));
                        eventHub.addEvent(event2, Channel.DRAW_PILE);
                    }
                    this.firstRound = false;
                } else {
                    event.finished(new InvalidCardException());
                }
                break;
            case "sync": sync(command); break;
        }
    }

    /**
     * Parse the incoming sync event and update accordingly
     *
     * @param command The incoming sync request
     */
    private void sync(Command command) {
        try {
            PlayPile playPile = new PlayPile().fromJSON(command.getArgs().get("playPile"));
            this.model.clear();
            this.model.addAll(playPile);
            view.updateView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method is supposed to be called after a new card is added to the pile. This method deals with the case
     * where we have a special card (Power card) and something needs to change. For example "NEW_COLOR".
     * This method is called upon last in addCard, if we successfully added a new card to the PlayPile.
     * If the played card wasn't a special card, nothing happens in here.
     */
    public void dealWithSpecialCards() {
        if (model.getTopCard().isPowerCard()) {
            if (model.getTopCard().getType() == Card.PowerCardType.NEW_COLOR || model.getTopCard().getType() == Card.PowerCardType.DRAW_4) {
                //Insert indication for choosing a color from color-picker here. Then change the color of the card.
                Color newColor;
                ObserverEvent event = new ObserverEvent(CommandFactory.changeColorCard());
                eventHub.addEvent(event, Channel.COLOR_PICKER);
                try {
                    event.waitForFinish(60); //waits 60 seconds before game craches
                    Command response = event.getResponse();
                    if (response.getArgs().get("color").equals(Color.RED.toString())) {
                        newColor = Color.RED;
                        model.getTopCard().setColor(newColor);
                    } else if (response.getArgs().get("color").equals(Color.BLUE.toString())) {
                        newColor = Color.BLUE;
                        model.getTopCard().setColor(newColor);
                    } else if (response.getArgs().get("color").equals(Color.GREEN.toString())) {
                        newColor = Color.GREEN;
                        model.getTopCard().setColor(newColor);
                    } else if (response.getArgs().get("color").equals(Color.YELLOW.toString())) {
                        newColor = Color.YELLOW;
                        model.getTopCard().setColor(newColor);
                    }
                    eventHub.addEvent(new ObserverEvent(response), Channel.COMMUNICATION_CAPTAIN);
                } catch (InterruptedException | BaseException e) {
                    System.out.println("You waited to long to pick a color!");
                }
                //this happens after a new color has been picked
                view.updateView();
            }
            if (model.getTopCard().getType() == Card.PowerCardType.DRAW_4) {
                //Notify the server(Com.Cap) that we want to add 4 cards to the NEXT players hand
                ObserverEvent event = new ObserverEvent(CommandFactory.draw4Cards());
                eventHub.addEvent(event, Channel.COMMUNICATION_CAPTAIN);

            } else if (model.getTopCard().getType() == Card.PowerCardType.DRAW_2) {
                //Notify the server(Com.Cap) that we want to add 2 cards to the NEXT players hand
                ObserverEvent event = new ObserverEvent(CommandFactory.draw2Cards());
                eventHub.addEvent(event, Channel.COMMUNICATION_CAPTAIN);

            } else if (model.getTopCard().getType() == Card.PowerCardType.BLOCK) {
                //Notify the server(Com.Cap) that we want to BLOCK the next player from making a move
                //put the second next players turn to true instead
                ObserverEvent event = new ObserverEvent(CommandFactory.block());
                eventHub.addEvent(event, Channel.COMMUNICATION_CAPTAIN);

            } else if (model.getTopCard().getType() == Card.PowerCardType.REVERSE) {
                //Notify the server(Com.Cap) that we want to change the direction of the players turn.
                ObserverEvent event = new ObserverEvent(CommandFactory.reverse());
                eventHub.addEvent(event, Channel.COMMUNICATION_CAPTAIN);
            }
        }
    }

    /**
     * @return view of this PLayPile
     */
    public PlayPileView getPlayPileView() {
        return this.view;
    }

    /**
     * @return model of this PLayPile
     */
    public PlayPile getPlayPileModel() {
        return this.model;
    }
}
