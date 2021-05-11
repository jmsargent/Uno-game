package game.controllers;
import game.views.ColorPickerView;
import network.Command;
import network.CommandFactory;
import observer.Channel;
import observer.ObserverEvent;
import observer.EventHub;
import observer.Observer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ColorPickerController receives requests for a ColorPickerview to appear and return whichever color is chosen
 * @author Johanna Schüldt
 * @version 2021-03-05
 */


public class ColorPickerController extends Observer implements ActionListener {
    private ColorPickerView colorPicker;
    private EventHub eventHub;
    ObserverEvent event;


    /**
     * Constructs a new ColorPickerController
     * @param eventHub
     */
    public ColorPickerController(EventHub eventHub) {
        colorPicker = new ColorPickerView();
        this.eventHub = eventHub;
        eventHub.subscribe(this, Channel.COLOR_PICKER);
        colorPicker.getGreen().addActionListener(this);
        colorPicker.getBlue().addActionListener(this);
        colorPicker.getRed().addActionListener(this);
        colorPicker.getYellow().addActionListener(this);
        colorPicker.getGreen().setActionCommand("green");
        colorPicker.getBlue().setActionCommand("blue");
        colorPicker.getRed().setActionCommand("red");
        colorPicker.getYellow().setActionCommand("yellow");
        colorPicker.setVisible(false);
    }

    /**
     * Listens to button presses from view, and sends the chosen color back as a message to PlayPileController
     * @param e is an ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String color = e.getActionCommand();            //TODO Eventuellt flytta dessa tre rader till update
        Command command = event.getCommand();
        if (command.getName().equals("change_color")){
            Command response = switch (color) {
                case "green" -> CommandFactory.changeColorResponse(Color.GREEN);
                case "blue" -> CommandFactory.changeColorResponse(Color.BLUE);
                case "red" -> CommandFactory.changeColorResponse(Color.RED);
                case "yellow" -> CommandFactory.changeColorResponse(Color.YELLOW);
                default -> null;
            };
            event.finished(response);
        }
        colorPicker.setVisible(false);
        event = null;                       //Behövs någon typ av nollställning? TODO Ta bort (eventuellt)
    }

    /**
     * Listens to events and makes the colorPicker visible
     */
    @Override
    public void update() {
        event = eventHub.getEvent(Channel.COLOR_PICKER);
        colorPicker.setVisible(true);       //Funkar denna? TODO
    }
}
