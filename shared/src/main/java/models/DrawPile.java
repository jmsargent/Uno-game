package models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;


/**
 * Initialize the entire deck of cards, this class becomes the pile which players draw cards from.
 * The first card is the card at the top, aka the next card to be drawn.
 * @author lisal
 * @version 2021-03-07
 */
public class DrawPile extends CardPile {

    public Color[] colors = new Color[4];
    public Card.PowerCardType[] type = new Card.PowerCardType[5];
    private CardPile playedCards = new CardPile();

    public DrawPile() {}

    /**
     * Create a whole deck of UNO cards according to the UNO rules.
     */
    public void createUNODeck(){
        colorArray();
        //first: make 20 cards 0-9 in the colors: blue, red, green and yellow
        for(Color color : colors) {
            for (int i = 0; i < 10; i++) {
                Card card = new Card(color, i);
                //Add the card to this class/list 2 times (the CardPile aka all the cards in the beginning of the game)
                add(card);
                if(i != 0){
                    add(card);
                }
            }
        }
        //now we want, for each color: 2 block, 2 reverse, 2 draw_2
        typeArray();
        for(Color color : colors) {
            for(int i=0; i<3; i++) {
                Card card = new Card(color, type[i]);
                add(card);
                //there's only one zero-card of each color
                if(i != 0) {
                    add(card);
                }
            }
        }

        //at last, add 4 change color cards & 4 draw-4 cards
        for(int i=0; i<4; i++) {
            Card card = new Card(Color.BLACK, type[3]);
            add(card);
            Card card2 = new Card(Color.BLACK, type[4]);
            add(card2);
        }
        //NOW SHUFFLE THE PILE! :)
        shuffle();
    }

    /**
     * @return c which is the drawn card (from the top of the pile)
     */
    public Card draw() {
        Card c = this.getFirst();
        //remove the first card in the pile, the card we just returned!
        remove();
        //check if we need to add cards to the DrawPile by always calling the reshuffle method
        reShufflePlayPile();
        return c;
    }

    /**
     * This is the same as the draw method above, but here we can choose how many cards we want to draw.
     * @param num number of cards to draw.
     * @return the drawn cards in the form of an array
     */
    public Card[] draw(int num) {
        Card[] c = new Card[num];
        for(int i=0; i<num; i++){
            c[i] = draw();
        }
        return c;
    }

    /**
     * Shuffle the whole dock of cards, three times to make sure
     */
    public void shuffle() {
        Collections.shuffle(this);
        Collections.shuffle(this);
        Collections.shuffle(this);
    }

    /**
     * Shuffles used cards into the empty DrawPile.If for some reason we don't have any already played cards,
     * we create a new pile from scratch. Then ignoring if players may have certain cards at hand already.
     */
    public void reShufflePlayPile() {
        if(this.isEmpty()) {
            if (playedCards.isEmpty()) {
                createUNODeck();
            } else { //else = if it works like it should
                addAll(this.playedCards);
                shuffle();
            }
        }
    }

    /**
     * Add a card to the playedCard list
     * @param card is the card we want to add to the list
     */
    public void addPlayedCard(Card card){
        playedCards.add(card);
    }

    /**
     * This help-method only initiates the types of powercards in the powercard-type array
     */
    private void typeArray(){
        type[0] = Card.PowerCardType.BLOCK;
        type[1] = Card.PowerCardType.REVERSE;
        type[2] = Card.PowerCardType.DRAW_2;
        type[3] = Card.PowerCardType.DRAW_4;
        type[4] = Card.PowerCardType.NEW_COLOR;
    }

    /**
     * This help-method only initiates the colors in the colors-array
     */
    private void colorArray(){
        colors[0] = Color.red;
        colors[1] = Color.blue;
        colors[2] = Color.green;
        colors[3] = Color.yellow;
    }

    @Override
    @JsonCreator
    public DrawPile fromJSON(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, DrawPile.class);
    }
}
