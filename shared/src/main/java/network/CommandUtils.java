package network;

import models.Card;
import models.PlayerModel;
import tools.ActiveGame;
import tools.SavedGame;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

/**
 * Some util functions to exctract data from the arguments
 *
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class CommandUtils {
    public static LinkedList<PlayerModel> getPlayers(Command command) {
        LinkedList<PlayerModel> players = new LinkedList<>();
        for (Map.Entry<String, String> entry : command.getArgs().entrySet()) {
            String key = entry.getKey();
            if (key.equals("players")) {
                String[] pJSON = entry.getValue().split("\\{");
                for (String s : pJSON) {
                    if (s.trim().equals("")) {
                        continue;
                    }
                    String[] parts = s.split(",");
                    PlayerModel player = new PlayerModel();
                    for (String part : parts) {
                        if (part.startsWith("\"name\":")) {
                            player.setName(part.trim().substring(7).replace("\"",""));
                        } else if (part.startsWith("\"card_count\":")) {
                            player.setOverriddenCardCount(Integer.parseInt(part.trim().substring(13).replace("\"","")));
                        } else if (part.startsWith("\"id\":")) {
                            player.setId(UUID.fromString(part.trim().substring(5).replace("\"","")));
                        } else if (part.startsWith("\"turn\":")) {
                            player.setPlayersTurn(Boolean.parseBoolean(part.trim().substring(7).replace("\"","")));
                        } else if (part.startsWith("\"uno\":")) {
                            player.setCalledUno(Boolean.parseBoolean(part.trim().substring(6).replace("\"","")));
                        }
                    }
                    players.add(player);
                }
            }
        }
        return players;
    }

    public static Card[] getCards(Command command) {
        LinkedList<Card> cards = new LinkedList<>();
        for (Map.Entry<String, String> entry : command.getArgs().entrySet()) {
            if (entry.getKey().startsWith("card")) {
                try {
                    cards.add(new Card().fromJSON(entry.getValue()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return cards.toArray(Card[]::new);
    }

    public static ActiveGame[] getActiveGames(Command command) {
        LinkedList<ActiveGame> games = new LinkedList<>();
        for (Map.Entry<String, String> entry : command.getArgs().entrySet()) {
            if (entry.getKey().startsWith("game")) {
                try {
                    games.add(new ActiveGame().fromJSON(entry.getValue()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return games.toArray(ActiveGame[]::new);
    }

    public static SavedGame[] getSavedGames(Command command) {
        LinkedList<SavedGame> games = new LinkedList<>();
        for (Map.Entry<String, String> entry : command.getArgs().entrySet()) {
            if (entry.getKey().startsWith("game")) {
                try {
                    games.add(new SavedGame().fromJSON(entry.getValue()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return games.toArray(SavedGame[]::new);
    }
}
