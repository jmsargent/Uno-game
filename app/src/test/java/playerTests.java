//import game.controllers.PlayerController;
//import game.views.PlayerView;
//import models.Card;
//import models.CardPile;
//import models.DrawPile;
//import models.PlayerModel;
//import observer.EventHub;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.UUID;
//import java.util.concurrent.ThreadLocalRandom;
//
//
//public class playerTests {
//    public static void main(String[] args) {
//        //fullPlayerTest();
//        // test2();
//        //valueSortTest();
//        //terminalColorSortTest();
//        playerAddCardTest();
//    }
//
//    private static void playerAddCardTest(){
//        DrawPile drawPile = new DrawPile();
//        CardPile cardPile = createCardPileFromDrawPile(7,drawPile);
//        PlayerModel playerModel = new PlayerModel("aero", cardPile, true);
//        PlayerView playerView = new PlayerView(playerModel);
//        PlayerController playerController = new PlayerController(drawPile,playerModel, playerView,new EventHub());
//
//        JFrame frame = new JFrame();
//        frame.setSize(new Dimension(1200, 300));
//        frame.add(playerView);
//        frame.setVisible(true);
//    }
//
//    private static CardPile createCardPileFromDrawPile(int nrOfCards,DrawPile drawPile){
//        CardPile cardPile = new CardPile();
//        for(int i=0; i< nrOfCards; ++i){
//            cardPile.add(drawPile.draw());
//        }
//        return cardPile;
//    }
//
//    private static void terminalColorSortTest() {
//        CardPile hand = createHand(10, false);
//        for(Card c : hand){
//            System.out.println(c.getColor());
//        }
//        hand.colorSort();
//        System.out.println("After sort:");
//
//        for(Card c : hand){
//            System.out.println(c.getColor());
//        }
//
//    }
//
//    private static void valueSortTest(){
//
//        CardPile hand = createHand(10,true);
//
//        for(Card c : hand){
//            System.out.println(c.getValue());
//        }
//
//        hand.valueSort();
//        System.out.println("After sort:");
//
//        for(Card c : hand){
//            System.out.println(c.getValue());
//        }
//    }
//
//
//
//    private static void oldStupidTest() {
//        Card c1 = new Card(Color.RED, 1);
//        Card c2 = new Card(Color.BLUE, 9);
//
//        if(c1.compareTo(c2) < 0){
//            System.out.println("c1 has less value than c2");
//        }
//
//        c1.setCompareColor(true);
//        c2.setCompareColor(true);
//
//        if(c1.compareTo(c2) > 0){
//            System.out.println("c2 has a bigger color value than c1");
//        }
//    }
//
//    private static void fullPlayerTest(){
//        CardPile cardPile = createHand(30, false);
//        PlayerModel playerModel = new PlayerModel("aero", cardPile, true);
//        PlayerView playerView = new PlayerView(playerModel);
//        PlayerController playerController = new PlayerController(playerModel, playerView,new EventHub(), UUID.randomUUID());
//
//        JFrame frame = new JFrame();
//        frame.setSize(new Dimension(1200, 300));
//        frame.add(playerView);
//        frame.setVisible(true);
//    }
//
//    static CardPile createHand(int cards, boolean withPowercards) {
//
//        CardPile cardPile = new CardPile();
//
//        for (int i = 0; i < cards; i++) {
//            int random = Math.abs(ThreadLocalRandom.current().nextInt());
//            int randVal = Math.abs(ThreadLocalRandom.current().nextInt());
//
//            System.out.println("Color random :" + random);
//            System.out.println("number random :" + randVal);
//
//            if (!withPowercards) {
//                if (random % 4 == 1) {
//                    //yellow
//                    cardPile.add(new Card(Color.YELLOW, (randVal % 9)));
//                } else if (random % 4 == 2) {
//                    // green
//                    cardPile.add(new Card(Color.GREEN, (randVal % 9)));
//                } else if (random % 4 == 3) {
//                    //red
//                    cardPile.add(new Card(Color.RED, (randVal % 9)));
//                } else {
//                    // blue
//                    cardPile.add(new Card(Color.BLUE, (randVal % 9)));
//                }
//            }
//            if (withPowercards) {
//
//                if (random % 10 < 4) {
//
//                    if (random % 4 == 1) {
//                        //yellow
//                        cardPile.add(new Card(Color.YELLOW, Card.PowerCardType.BLOCK));
//                    } else if (random % 4 == 2) {
//                        // green
//                        cardPile.add(new Card(Color.GREEN, Card.PowerCardType.NEW_COLOR));
//                    } else if (random % 4 == 3) {
//                        //red
//                        cardPile.add(new Card(Color.RED, Card.PowerCardType.DRAW_2));
//                    } else {
//                        // blue
//                        cardPile.add(new Card(Color.BLUE, Card.PowerCardType.REVERSE));
//                    }
//                } else {
//                    if (random % 4 == 1) {
//                        //yellow
//                        cardPile.add(new Card(Color.YELLOW, (randVal % 9)));
//                    } else if (random % 4 == 2) {
//                        // green
//                        cardPile.add(new Card(Color.GREEN, (randVal % 9)));
//                    } else if (random % 4 == 3) {
//                        //red
//                        cardPile.add(new Card(Color.RED, (randVal % 9)));
//                    } else {
//                        // blue
//                        cardPile.add(new Card(Color.BLUE, (randVal % 9)));
//                    }
//                }
//            } // if end
//        } // for end
//        return cardPile;
//    }
//}