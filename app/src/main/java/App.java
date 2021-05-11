import game.GameController;
import menu.MenuController;
import observer.Channel;
import observer.EventHub;
import observer.Observer;
import observer.ObserverEvent;
import tools.CommunicationCaptain;
import tools.Settings;

import java.io.IOException;

public class App extends Observer {
    // aka Main,

    GameController gc;
    MenuController mc;
    CommunicationCaptain cc;
    Settings settings;
    EventHub eventHub = new EventHub();

    public void run() throws IOException {
        try {
            settings = new Settings();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        eventHub.subscribe(this, Channel.APP);
        cc = new CommunicationCaptain(settings, eventHub);
        cc.start();
        mc = new MenuController(cc, settings, eventHub);
    }

    public static void main(String[] args) {
        App app = new App();
        try {
            app.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        ObserverEvent event = eventHub.getEvent(Channel.APP);
        switch (event.getCommand().getName()) {
            case "start_game" -> {
                mc.hide();
                gc = new GameController(eventHub, settings);
                event.finished();
            }
            case "disconnect" -> {
                gc.close();
                mc.show();
            }
            default -> System.out.println("Unknown command: " + event.getCommand().getName());
        }
    }
}
