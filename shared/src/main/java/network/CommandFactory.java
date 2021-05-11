package network;

import com.fasterxml.jackson.core.JsonProcessingException;
import models.*;

import java.awt.*;
import java.util.UUID;

/**
 * This class is used to easily generate Command classes to send between the server and client
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class CommandFactory {

    private CommandFactory() {
    }

    /**
     * @param playerName is the name of the player as a string
     * @param gameID is the gameID
     * @return a string with jain_game, and string of player_name & game_id
     */
    public static Command joinGame(String playerName, UUID gameID) {
        return new Command("join_game", "player.name:" + playerName, "game.id:" + gameID.toString());
    }

    /**
     * @return command "ping"
     */
    public static Command ping() {
        return new Command("ping");
    }

    /**
     * @param playerName is the name of the player as a string
     * @return "new_game" and string of playerName
     */
    public static Command newGame(String playerName) {
        return new Command("new_game", "player.name:" + playerName);
    }

    /**
     * @param clientID is the ID
     * @return reconnect and string of player/client id
     */
    public static Command reconnect(UUID clientID) {
        return new Command("reconnect", "player.id:" + clientID.toString());
    }

    /**
     * @param gameID is the gameID nr
     * @return "connection_confirmation" and string of gameID
     */
    public static Command connectionConfirmation(UUID gameID) {
        return new Command("connection_confirmation", "game.id:" + gameID.toString());
    }

    /**
     * @param hand is a cardPile
     * @return the cardPile hand
     */
    public static Command syncPlayerHand(CardPile hand) {
        return new Command("sync_player_hand", hand.toCommandArguments());
    }

    /**
     * @param card is the card we want to play
     * @return "play_card" and what card should be played in string format
     */
    public static Command playCard(Card card) {
        return new Command("play_card", "card_played:"+card.toString());
    }

    /**
     * @param card is the card we want to save
     * @return "save_card" and what card should be saved, in string format
     */
    public static Command saveCard(Card card) {
        return new Command("save_card", "card:"+card.toString());
    }

    /**
     * @return command "change_color"
     */
    public static Command changeColorCard() {
        return new Command("change_color");
    }

    /**
     * @param color is the color we want to change to
     * @return command "change_color_response" and the color in string format
     */
    public static Command changeColorResponse(Color color) {return new Command("change_color_response", "color:"+color.toString());}

    /**
     * @param player is a PlayerModel
     * @return "updateCardView" and the playerModel in string format
     */
    public static Command updateCardView(PlayerModel player){
        return new Command("updateCardView", "player:"+player.toString());
    }


    /**
     * @param player is a PlayerModel
     * @return "updateCurrentPlayerView" and the PlayerModel in string format
     */
    public static Command updateCurrentPlayerView(PlayerModel player){
        return new Command("updateCurrentPlayerView", "player:"+player.toString());
    }

    /**
     * @author Jonathan Sargent
     * @version 2021-03-06
     * @param playerturn
     * @return
     */
    public static Command setPlayerTurn(boolean playerturn){
        return new Command("turn_status","your_turn:"+Boolean.toString(playerturn));
    }

     /**
     * @return the command draw_card and what card should be drawn, in string format.
     */
    public static Command drawCard(Card card) {
        return new Command("draw_card","card_drawn:"+card.toString());
    }

    /**
     * This command is what is used for syncing the clients and the server
     * @param drawPile the state of the drawPile
     * @param playPile the state of the playPile
     * @param players the state of the players
     * @return the full command
     * @throws JsonProcessingException
     */
    public static Command gameSync(DrawPile drawPile, PlayPile playPile, PlayerModel[] players) throws JsonProcessingException {
        CommandArguments ca = new CommandArguments();
        String pJSON = "";
        for (PlayerModel player : players) {
            pJSON += "{" +
                    "\"name\":\""+player.getName()+"\"," +
                    "\"card_count\":"+ player.numberOfCards() +"," +
                    "\"id\":\""+player.getId().toString()+"\"," +
                    "\"turn\":"+player.isPlayersTurn()+"," +
                    "\"uno\":"+player.isCalledUno()+"" +
                    "}";
        }
        pJSON = pJSON.substring(0,pJSON.length()-1);
        ca.put("players", pJSON);
        ca.put("drawPile",drawPile.toJson());
        ca.put("playPile", playPile.toJson());

        return new Command("sync", ca);
    }

    /**
     * @return command "draw_4"
     */
    public static Command draw4Cards() {
        return new Command("draw_4");
    }
    /**
     * @return command "draw_2"
     */
    public static Command draw2Cards() {
        return new Command("draw_2");
    }
    /**
     * @return command "block"
     */
    public static Command block() {
        return new Command("block");
    }
    /**
     * @return command "reverse"
     */
    public static Command reverse() {
        return new Command("reverse");
    }

    /**
     * @return "drawn_cards" and the cards
     */
    public static Command drawnCards(Card... cards) throws JsonProcessingException {
        CommandArguments args = new CommandArguments();
        int i = 0;
        for (Card card : cards) {
            args.put("card." + i, card.toJson());
            i++;
        }
        return new Command("drawn_cards", args);
    }

    /**
     * Calls uno
     * @return
     */
    public static Command callUno(){
        return new Command("uno");
    }

    /**
     * @return the command "save", this indicates that the game should be saved.
     */
    public static Command save() {
        return new Command("save");
    }

    /**
     * @param gameID is the gameID
     * @return the game id in string format.
     */
    public static Command load(UUID gameID) {
        return new Command("load_game","game.id:" + gameID.toString());
    }

    /**
     * @return start_game
     */
    public static Command startGame() {
        return new Command("start_game");
    }
    /**
     * @return game_started
     */
    public static Command gameStarted() {
        return new Command("game_started");
    }
    /**
     * @return won_game
     */
    public static Command GameIsWon() {
        return new Command("won_game");
    }
    /**
     * @param gameID is the gameID
     * @return game_joined and what game by gameID as a string
     */
    public static Command gameJoined(UUID gameID) {return new Command("game_joined", "game.id:"+gameID.toString());}

    /**
     * @return disconnect
     */
    public static Command disconnect() { return new Command("disconnect");
    }

    public static Command getActiveGames() {
        return new Command("get_active_games");
    }

    public static Command getSavedGames(UUID clientID) {
        return new Command("get_saved_games", "player.id:"+clientID.toString());
    }
}
