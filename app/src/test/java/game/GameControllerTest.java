//package game;
//
//import game.views.PlayerView;
//import models.Card;
//import models.CardPile;
//import models.DrawPile;
//import models.PlayerModel;
//
//import java.awt.*;
//import java.util.concurrent.ThreadLocalRandom;
//
//class GameControllerTest{
//    public static void main(String[] args){
//        DrawPile drawPile = new DrawPile();
//        CardPile cardPile = createCardPileFromDrawPile(10,drawPile);
//
//        PlayerModel playerModel = new PlayerModel("qwoke",cardPile,true);
//        PlayerView playerView = new PlayerView(playerModel);
//        GameController gameController1 = new GameController(playerModel,playerView);
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
//    // Creates a random testerhand
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
//                    if(cardPile.size() - cards > 1){
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
//                    }}else if (cardPile.size() - cards == 2){
//                     cardPile.add(new Card(Color.BLACK, Card.PowerCardType.DRAW_4));
//                    }else if (cardPile.size() - cards == 1){
//                        cardPile.add(new Card(Color.BLACK, Card.PowerCardType.NEW_COLOR));
//                    }
//                }
//            } // if end
//        } // for end
//        return cardPile;
//    }
//}