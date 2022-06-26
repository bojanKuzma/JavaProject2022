package org.unibl.etf.util;

import javafx.scene.image.Image;
import org.unibl.etf.Main;
import org.unibl.etf.models.card.Card;
import org.unibl.etf.models.card.NormalCard;
import org.unibl.etf.models.card.SpecialCard;
import org.unibl.etf.models.game.Game;
import org.unibl.etf.models.pawn.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class RandomGenerator {
    private static final int MAX_HOLES_ON_MAP = 10;
    private static final int NUM_OF_CARDS = 52;
    private static final int NUM_OF_STANDARD_CARDS = 10;
    private static final int ONE_FIELD = 1;
    private static final int TWO_FIELDS = 2;
    private static final int THREE_FIELDS = 3;
    private static final int FOUR_FIELDS = 4;
    private static final int MAX_PAWNS = 4;
    private static final double NORMAL_PAWN_CHANCE = 0.3333;
    private RandomGenerator(){}

    public static LinkedList<Pawn> generatePawns(String color, String playerName){
        LinkedList<Pawn> generatedPawnList = new LinkedList<>();
        for (int i = 0; i < MAX_PAWNS; i++) {
            double chance = Math.random();
            if(chance <= NORMAL_PAWN_CHANCE)
                generatedPawnList.add(new StandardPawn(
                        Color.valueOf(color),
                        new Image(String.valueOf(Main.class.getResource("img/bear/" + color + ".png"))),
                        i + 1
                        )
                );
            else if(chance <= 2 * NORMAL_PAWN_CHANCE)
                generatedPawnList.add(new FlyingPawn(
                                Color.valueOf(color),
                                new Image(String.valueOf(Main.class.getResource("img/eagle/" + color + ".png"))),
                                i + 1
                        )
                );
            else
                generatedPawnList.add(new FastPawn(
                                Color.valueOf(color),
                                new Image(String.valueOf(Main.class.getResource("img/horse/" + color + ".png"))),
                                i + 1
                        )
                );
        }
        Player player = new Player();
        player.pawns = new LinkedList<>(generatedPawnList);
        player.name = playerName;
        Game.players.add(player);
        return  generatedPawnList;
    }

    public static LinkedList<Card> generateDeck(){
        LinkedList<Card> cards = new LinkedList<>();
        Random random = new Random();
        for (int i = 0; i < NUM_OF_CARDS; i++) {
            if(i < NUM_OF_STANDARD_CARDS)
                cards.add(new NormalCard(
                        new Image(String.valueOf(Main.class.getResource("img/cards/1.png"))),
                        ONE_FIELD
                        )
                );
            else if(i < TWO_FIELDS * NUM_OF_STANDARD_CARDS){
                cards.add(new NormalCard(
                                new Image(String.valueOf(Main.class.getResource("img/cards/2.png"))),
                                TWO_FIELDS
                        )
                );
            }
            else if(i < THREE_FIELDS * NUM_OF_STANDARD_CARDS){
                cards.add(new NormalCard(
                                new Image(String.valueOf(Main.class.getResource("img/cards/3.png"))),
                                THREE_FIELDS
                        )
                );
            }
            else if( i < FOUR_FIELDS * NUM_OF_STANDARD_CARDS){
                cards.add(new NormalCard(
                                new Image(String.valueOf(Main.class.getResource("img/cards/4.png"))),
                                FOUR_FIELDS
                        )
                );
            }
            else
                cards.add(new SpecialCard(
                        new Image(String.valueOf(Main.class.getResource("img/cards/5.png"))),
                        random.nextInt(MAX_HOLES_ON_MAP) + 2
                        )
                );

        }
        Collections.shuffle(cards);
        return cards;
    }
//todo izmjeni logiku
    public static ArrayList<Integer> getFields(Card c){
        Random random = new Random();
        ArrayList<Integer> result = new ArrayList<>();
        int counter = 0;
        while(counter < c.getNumber()) {
            int x = random.nextInt(ConfigReader.mapSize + 1);
            int y = random.nextInt(ConfigReader.mapSize + 1);
            int calculated = x * ConfigReader.mapSize + y;
            while(result.contains(calculated) || calculated > ConfigReader.mapSize * ConfigReader.mapSize - 1){
                x = random.nextInt(ConfigReader.mapSize + 1);
                y = random.nextInt(ConfigReader.mapSize + 1);
                calculated = x * ConfigReader.mapSize + y;
            }
            result.add(calculated);
            counter++;
        }
        return result;
    }
}
