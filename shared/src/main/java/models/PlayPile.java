package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.IOException;
/**
 * This class is the model class for the PlayPile. It is empty before the first card is played.
 * The last card is the card at top (shown).
 * @author lisal
 * @version 2021-03-07
 */
public class PlayPile extends CardPile{

    Card newCard; //this is the new card that we need to add to this class (the PlayPile) and put on top if it is a valid card!
    private Card playedCard;

    public PlayPile(){}

    public void initialize() {
        //The start-card is a white card with value 0, any card can be played on this card
        Card c = new Card(Color.WHITE, 0);
        addFirst(c);
    }

    /**
     * Check if adding the new Card is a valid move.If it is, add the card to top of the pile
     * (aka last in this LinkedList class).
     * If successfully adding the card, add the old first card to the "playedCard" list, to potentially reuse later in the game.
     * @param c is the card we want to try adding to the pile (the card the player wants to play)
     */
    public void addCard(Card c) {
        this.newCard=c;
        //code for adding the card to the pile (Not: updating the view. This is done from control after calling this method)
        if(isValidPlay(newCard)) {
            addLast(newCard);
            //After (successfully) adding the new card to the (list)class, we want to remove the old one and put it in the playedCard list
            //Not if color white is because we don't want to include the "starter card"
            if(this.getFirst().getColor() != Color.WHITE) {
                //also, if the card is a special card that used to be black (change color cards) then change color back to black
                if(this.getFirst().getType() == Card.PowerCardType.NEW_COLOR || this.getFirst().getType() == Card.PowerCardType.DRAW_4){
                    this.getFirst().setColor(Color.BLACK);
                }
                //set the old topcard as the playedCard, this will be retrieved by the drawpile later
                playedCard = this.getFirst();
            }
            this.removeFirst();
        }
    }

    /**
     * This method checks if the card the player wants to add to the playpile is a valid card.
     * For example following the current color/number, or is a special card with special rules.
     * @param newCard is the card some player is trying to play
     * @return true if the played card is a valid card to play right now
     */
    public boolean isValidPlay(Card newCard) {
        //in the beginning of the game, the list(this class) will be empty! Any card will be ok.
        if(this.isEmpty()){
            return true;
        }
        //Default card is a white card, we can play any card on this card
        if(getTopCard().getColor() == Color.WHITE){
            return true;
        }
        //if newCard is a special card that is black(Draw_4 or Change_Color), it is always playable.
        else if(newCard.getColor() == Color.BLACK) {
            return true;
        }
        //Else we need to have either color or value matching for it to be a valid card to play
        else if(newCard.getColor() == getTopCard().getColor()){
                return true;
        }
        else if(newCard.getValue() == getTopCard().getValue()){
            return true;
        }else {
            return false;
        }
    }

    @Override
    @JsonCreator
    public PlayPile fromJSON(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, PlayPile.class);
    }

    /**
     * get method for playedCard, aka not the card on top but the card on top before the current one.
     * @return the latest playedCard
     */
    public Card getPlayedCard(){
        return this.playedCard;
    }

    /**
     * This method returns the last card of the PlayPile. If we want it to return a new card, we need to call upon the
     * addCard method first.
     * @return card at the top of the pile
     */
    public Card getTopCard(){
        return this.getLast();
    }
}
