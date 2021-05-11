package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import interfaces.ToCommandArguments;
import interfaces.ToJSON;
import network.CommandArguments;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Jonathan Sargent
 * @version 2021-03-05
 * Player MVC Handles player interaction with their cards, and the buttons around their hand (Sort, UNO! etc)
 */
public class PlayerModel implements ToJSON, ToCommandArguments {
    private CardPile hand;
    private String name;
    private boolean playersTurn;
    private boolean calledUno;
    private int overriddenCardCount = -1;
    private UUID id;

    /**
     * @param name The player name
     * @param hand the starting hand of the new player
     */
    public PlayerModel(String name, CardPile hand, UUID id, boolean playersTurn) {
        this.hand = hand;
        this.name = name;
        this.playersTurn = playersTurn;
        this.calledUno = false;
    }

    /**
     * Only to be used when using the fromJSON method
     */
    public PlayerModel() {
    }

    /**
     * Returns true if player Called uno
     * @return boolean
     */
    public boolean isCalledUno() {
        return calledUno;
    }

    /**
     * Toggles calledUno boolean variable
     */
    public void isCalledUno(boolean uno){
        calledUno = uno;
    }

    /**
     * The number of cards the player currently possess
     * @return int
     */
    public int numberOfCards() {
        if (overriddenCardCount != -1) {
            return overriddenCardCount;
        }
        return hand == null ? 0 : hand.size();
    }


    /**
     * Returns the cards in the players hand
     * @return CardPile
     */
    public CardPile getHand() {
        return hand;
    }

    /**
     * Guess what? it gets the players name
     * @return String
     */
    public String getName() {
        return name;
    }


    /**
     * Add a new card to the player hand
     * @param card
     */
    public void addCard(Card card){
        hand.add(card);
    }

    /**
     * Remove a new card from the player hand
     * @param index of hand
     */
    public void removeCard(int index){
        hand.remove(index);
    }

    /**
     * sort the players hand by color (green, blue...) based on the dutch flag sorting algorithm
     */
    public void sortHandByColor() {
        this.hand.colorSort();
    }

    /**
     * sort the players hand by value (0, 1, 2...)
     */
    public void sortHandByValue() {
        this.hand.valueSort();
    }

    /**
     * Returns true if it is the players turn.
     * @return boolean
     */
    public boolean isPlayersTurn() {
        return playersTurn;
    }

    /**
     * Sets the players Turn
     * @param playersTurn
     */
    public void setPlayersTurn(Boolean playersTurn) {
        this.playersTurn = playersTurn;
    }

    @JsonCreator
    public PlayerModel fromJSON(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, PlayerModel.class);
    }

    @Override
    public CommandArguments toCommandArguments() {
        CommandArguments args = new CommandArguments();
        args.put("Player.Name", name);
        args.put("Player.Hand", hand.toString());
        args.put("Player.PressedUno", Boolean.toString(calledUno));
        args.put("Player.PlayersTurn", Boolean.toString(playersTurn));
        return args;
    }

    /**
     * Calls uno if size of hand == 1
     * @return true if uno is called otherwise false
     */
    public boolean tryCallUno() {
        if(this.hand.size()==1){
            calledUno = true;
        }
        return calledUno;
    }

    public void setOverriddenCardCount(int overriddenCardCount) {
        this.overriddenCardCount = overriddenCardCount;
    }

    public void setName(String value) {
        this.name = value;
    }

    public void setCalledUno(boolean parseBoolean) {
        this.calledUno = parseBoolean;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
