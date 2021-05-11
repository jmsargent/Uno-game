package game.controllers;

import exceptions.InvalidCardException;
import game.views.PlayerView;
import models.Card;
import models.CardPile;
import models.DrawPile;
import models.PlayerModel;
import network.CommandFactory;
import network.CommandUtils;
import observer.Channel;
import observer.EventHub;
import observer.Observer;
import observer.ObserverEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

/**
 * @author Jonathan Sargent
 * @version 2021-03-05
 * Player MVC handles player interaction with their cards,
 * and the buttons around their hand (Sort, UNO! etc)
 */
public class PlayerController extends Observer {
    private PlayerView playerView;
    private PlayerModel playerModel;
    private EventHub eventHub;
    private DrawPile drawPile; // TODO: remove when done with testing
    private UUID clientID;

    /**
     * Constructor initiating mvc pattern + observer for Game
     *
     * @param playerModel
     * @param playerView
     * @param eventHub
     * @author Jonathan Sargent
     * @version 2021-03-05
     * Constructor initiating mvc pattern + observer for Game
     */
    public PlayerController(PlayerModel playerModel, PlayerView playerView, EventHub eventHub, UUID clientID) {
        this.playerModel = playerModel;
        this.playerView = playerView;
        this.eventHub = eventHub;
        this.clientID = clientID;
        eventHub.subscribe(this, Channel.PLAYER);
        initActionListener();
    }

    /**
     * Adds actionlisteners to:
     * 1)  UnoButton
     * 2)  ColorSortButton
     * 3)  ValueSortButton
     */
    private void initActionListener() {
        this.playerView.getUnoButton().addActionListener(e -> tryCallUno());
        this.playerView.getColorSortButton().addActionListener(e -> sortByColor());
        this.playerView.getValueSortButton().addActionListener(e -> sortByValue());
        addCardActionListeners();
    }

    private void tryCallUno() {
        if (playerModel.getHand().size() == 1) {
            ObserverEvent unoEvent = new ObserverEvent(CommandFactory.callUno());
            eventHub.addEvent(unoEvent, Channel.COMMUNICATION_CAPTAIN);
            playerModel.isCalledUno(true);
            playerView.updateUnoButtonVisual();
        }
    }

    /**
     * Attaches numbered actionlisteners to cardVisuals
     */
    private void addCardActionListeners() {
        int cardPos = 0;
        for (JButton currentCardVis : this.playerView.getHandVisual()) {
            // Set actionCommand to their respective index
            currentCardVis.setActionCommand(Integer.toString(cardPos));
            currentCardVis.addActionListener(new CardVisListener());
            cardPos++;
        }
    }

    /**
     * Sort playerHand by values & rewrites View
     */
    public void sortByValue() {
        this.playerModel.sortHandByValue();
        this.playerView.rewriteView();
    }

    /**
     * Sort playerHand by colors & rewrites View
     */
    public void sortByColor() {
        this.playerModel.sortHandByColor();
        this.playerView.rewriteView();
    }

    /**
     * Remember player_turn for later
     * Attempts to playCard which holds position index in playerHand
     *
     * @param index
     */
    public void playCard(int index) {

        // if is players turn
        if (this.playerModel.isPlayersTurn()) {

            // check with playpile and communicationcaptain about playing the chosen card
            ObserverEvent event = new ObserverEvent(CommandFactory.playCard(this.playerModel.getHand().get(index)));
            ObserverEvent event2 = new ObserverEvent(CommandFactory.playCard(this.playerModel.getHand().get(index)));

            try {
                eventHub.addEvent(event2, Channel.PLAY_PILE);
                // if card color is not black, that means we wanna wait for finish for syncing reasons
                if (!this.playerModel.getHand().get(index).getColor().equals(Color.BLACK)) {
                    event2.waitForFinish(5);
                }
                eventHub.addEvent(event, Channel.COMMUNICATION_CAPTAIN);
                removeCard(index);
            } catch (InvalidCardException invalidCardException) {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Blindly adds a card to hand and attaches an actionListener to it
     * And sets playerturn to false
     *
     * @param card
     */
    private void addCard(Card card) {
        this.playerModel.addCard(card);
        this.playerView.updateHand();
        addCardActionListeners();

        if (this.playerModel.isCalledUno()) {
            this.playerModel.isCalledUno(false);
            this.playerView.updateUnoButtonVisual();
        }
    }

    /**
     * @param cards Blindly adds cardpile to hand
     */
    private void addCard(CardPile cards) {
        for (Card card : cards)
            addCard(card);
    }

    private void addCard(Card[] cards) {
        for (Card card : cards)
            addCard(card);
    }

    /**
     * Removes card with index without questioning, attaches new actionlisteners
     *
     * @param index
     */
    private void removeCard(int index) {
        this.playerModel.removeCard(index);
        this.playerView.updateHand();
        addCardActionListeners();
    }

    /**
     * Handles events connected to:
     * 1) DrawPile
     */
    @Override
    public void update() {
        ObserverEvent event = eventHub.getEvent(Channel.PLAYER);
        switch (event.getCommand().getName()) {
            case "draw_card" -> addCard(new Card(event.getCommand().getArgs().get("card_drawn")));
            case "drawn_cards" -> addCard(CommandUtils.getCards(event.getCommand()));
            case "turn_status" -> playerModel.setPlayersTurn(Boolean.valueOf(event.getCommand().getArgs().get("your_turn")));
        }
    }

    /**
     * Changes status of uno-button based on observerresponse, otherwise print that they may not.
     */
    private void changeUnoStatus(String observerResponse) {
        if (Boolean.valueOf(observerResponse)) {
            if (this.playerModel.isCalledUno()) {
                this.playerView.updateUnoButtonVisual();
            }
        } else {
            System.out.println("You may not call uno at this point");
        }
    }

    /**
     * Actionliseners for cards with number attached to them
     */
    private class CardVisListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            playCard(Integer.parseInt(e.getActionCommand()));
        }
    }
}
