package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import interfaces.ToCommandArguments;
import interfaces.ToJSON;
import network.CommandArguments;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

/**
 * A class for a LinkedList, here representing a pile of cards (DrawPile/PlayerHand)
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class CardPile extends LinkedList<Card> implements ToJSON, ToCommandArguments {

    /**
     * loop through the pile(s) in order to save them
     */
    public void savePile() {

    }

    /**
     * Sort by color
     */
    public void colorSort() {
        setColorSort(true);
        Collections.sort(this);
    }

    /**
     * Sort by value
     */
    public void valueSort() {
        setColorSort(false);
        Collections.sort(this);
    }

    /**
     * set true if the deck is to be compared with color as top priority and value second
     * false if value 1:st, color 2:nd
     *
     * @param colorSort
     */
    private void setColorSort(boolean colorSort) {
        for (Card c : this) {
            c.setCompareColor(colorSort);
        }
    }

    @JsonCreator
    public CardPile fromJSON(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, CardPile.class);
    }

    @Override
    public CommandArguments toCommandArguments() {
        CommandArguments args = new CommandArguments();
        int i = 0;
        for (Card card : this) {
            try {
                args.put("card." + i, card.toCommandArguments().toJson());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            i++;
        }
        return args;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this.toArray());
    }
}
