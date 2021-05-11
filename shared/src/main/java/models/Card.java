package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import interfaces.ToCommandArguments;
import interfaces.ToJSON;
import network.CommandArguments;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

/**
 * Class representing the logic behind an uno card. A Card always have a color and a value, sometimes also a type.
 * If it has a type it is a special card (draw 4, change color card etc).
 * @version 2021-03-07
 */
public class Card implements ToJSON, ToCommandArguments, Comparable<Card> {
    private int value;
    private Color color;
    private PowerCardType type;
    // makes color more valuable than value when sorting
    private boolean compareColor;

    /**
     * This is a normal Card.
     * @param color The color of the selected card, [Red,Yellow,Blue,Green]
     * @param i card value [0-9]
     */
    public Card(Color color, int i) {
        this.compareColor = true;
        this.value = i;
        this.color = color;
        this.type = null;
    }

    public Card(Color color, PowerCardType type) {
        this.compareColor = true;
        setPowerCardVal(type);
        this.color = color;
        this.type = type;
    }

    /**
     * copy constructor
     *
     * @param card
     */
    public Card(Card card) {

        if (card.isPowerCard()) {
            this.type = card.type;
        }
        this.value = card.value;
        this.color = card.color;
    }

    /**
     * Used when re-creating from json
     */
    public Card() {

    }

    /**
     * Declare a card through string
     * @author Jonathan Sargent
     * @version 2021-03-05
     * @param cardString:
     */
    public Card(String cardString) {

        int underscoreIndex = cardString.indexOf("_");
        int value = Integer.parseInt(cardString.substring(1 + underscoreIndex));
        String colorString = cardString.substring(0, underscoreIndex);

        switch (value) {
            case -1:
                this.type = PowerCardType.REVERSE;
                break;
            case -2:
                this.type = PowerCardType.BLOCK;
                break;
            case -3:
                this.type = PowerCardType.NEW_COLOR;
                break;
            case -4:
                this.type = PowerCardType.DRAW_2;
                break;
            case -5:
                this.type = PowerCardType.DRAW_4;
                break;
        }

        this.value = value;

        if (colorString.equals(Color.BLACK.toString())) {
            this.color = Color.BLACK;
        } else if (colorString.equals(Color.BLUE.toString())) {
            this.color = Color.BLUE;
        } else if (colorString.equals(Color.YELLOW.toString())) {
            this.color = Color.YELLOW;
        } else if (colorString.equals(Color.GREEN.toString())) {
            this.color = Color.GREEN;
        } else if (colorString.equals(Color.RED.toString())) {
            this.color = Color.RED;
        }
    }

    /**
     * Sets a value for powercard based on what powerCard, for sorting
     */
    private void setPowerCardVal(PowerCardType type) {

        if (type == PowerCardType.REVERSE) {
            this.value = -1;
        } else if (type == PowerCardType.BLOCK) {
            this.value = -2;
        } else if (type == PowerCardType.NEW_COLOR) {
            this.value = -3;
        } else if (type == PowerCardType.DRAW_2) {
            this.value = -4;
        } else if (type == PowerCardType.DRAW_4) {
            this.value = -5;
        }
    }

    /**
     * When true the highest order of comparison is color , value
     * when false it is: value , color
     *
     * @param compareColor
     */
    public void setCompareColor(boolean compareColor) {
        this.compareColor = compareColor;
    }

    /**
     * @return the type of the card
     */
    public PowerCardType getType() {
        return type;
    }

    /**
     * @return the value of the card
     */
    public int getValue() {
        return value;
    }

    /**
     * @return the color of the card
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color The new color you want the card to have
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Returns the card properties in a String format
     *
     * @return color_value
     */
    public String toString() {
        return color.toString() + "_" + value;
    }

    @Override
    public CommandArguments toCommandArguments() {
        CommandArguments args = new CommandArguments();
        args.put("Card.Color", color.toString());
        args.put("Card.Value", "" + value);
        args.put("Card.PowerCard", "" + Objects.requireNonNullElse(type, ""));
        return args;
    }

    /**
     * Compare method for cards.
     * @param c is the card we compare to
     */
    @Override
    public int compareTo(Card c) {
        if (this.compareColor) {
            return this.getColorVal() > c.getColorVal() ? 1 : this.getColorVal() < c.getColorVal() ? -1 : 0;
        }
        return this.value > c.value ? 1 : this.value < c.value ? -1 : 0;
    }

    /**
     * method for giving unique int values for each color.
     * @return int 0-4 depending on the color of the card using this method
     */
    private int getColorVal() {
        if (this.color.getRGB() == Color.BLACK.getRGB()) {
            return 0;
        } else if (this.color.getRGB() == Color.BLUE.getRGB()) {
            return 1;
        } else if (this.color.getRGB() == Color.GREEN.getRGB()) {
            return 2;
        } else if (this.color.getRGB() == Color.YELLOW.getRGB()) {
            return 3;
        } else { // color = RED
            return 4;
        }
    }

    /**
     * Equals method for Cards
     * @param o
     * @return true/false
     */
    public boolean equals(Object o) {
        // if same equal
        if (this == o)
            return true;
        // check if null
        if (o == null)
            return false;
        // check if same type
        if (getClass() != o.getClass())
            return false;
        // field comparison
        Card c = (Card) o;
        // this requires powercards to have a value assigned to them
        return c.getColor().equals(this.getColor()) && this.value == c.getValue();
    }

    /**
     * powercard method, returns the type in string format
     */
    public enum PowerCardType {
        BLOCK("block"),
        REVERSE("reverse"),
        DRAW_2("draw_2"),
        DRAW_4("draw_4"),
        NEW_COLOR("new_color");
        private final String type;

        /**
         * @param type is the string value of the type
         */
        private PowerCardType(String type) {
            this.type = type;
        }

        /**
         * @return type of the card
         */
        public String getType() {
            return this.type;
        }
    }

    /**
     * @return color_type
     */
    public String toString_type() {
        return color.toString() + "_" + type;
    }

    /**
     * check if the card is a powercard, aka has a type or not
     */
    @JsonIgnore
    public boolean isPowerCard() { return this.type != null; }

    @JsonSetter("color")
    public void setColor(int color) {
        if (color == Color.BLACK.getRGB()) {
            this.color = Color.BLACK;
        } else if (color == Color.BLUE.getRGB()) {
            this.color = Color.BLUE;
        } else if (color ==  Color.GREEN.getRGB()) {
            this.color = Color.GREEN;
        } else if (color ==  Color.YELLOW.getRGB()) {
            this.color = Color.YELLOW;
        } else if (color ==  Color.RED.getRGB()){ // color = RED
            this.color = Color.RED;
        } else {
            this.color = Color.WHITE;
        }
    }

    @JsonGetter("color")
    public Integer getColorJSON() {
        return color.getRGB();
    }

    @JsonCreator
    public Card fromJSON(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, Card.class);
    }
}