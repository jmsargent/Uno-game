package observer;

/**
 * Define the different channels here, so that you can't subscribe to a non-existent channel (This prevent typos)
 * The text inside the parenthesis is not important, it's just to make them different.
 * These channels are used by the EventHub(Observer)
 * @author marclaes
 * @version mars 7 2021
 */
public enum Channel {

    APP("app"),
    MAIN_MENU("main_menu"),
    DRAW_PILE("draw_pile"),
    UNO("uno"),
    COLOR_PICKER("color_picker"),
    PLAY_PILE("play_pile"),
    GAME_CONTROLLER("game_controller"),
    PLAYER("player"),
    PLAYER_LIST("player_list"),
    COMMUNICATION_CAPTAIN("coms_captain");
    private final String receiver;

    Channel(String receiver) {
        this.receiver = receiver;
    }
}