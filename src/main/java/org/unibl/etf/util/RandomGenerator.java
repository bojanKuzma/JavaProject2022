package org.unibl.etf.util;

import javafx.scene.image.Image;
import org.unibl.etf.Main;
import org.unibl.etf.models.pawn.*;

import java.util.LinkedList;

public class RandomGenerator {
    private static final int MAX_PAWNS = 3;
    private static final double NORMAL_PAWN_CHANCE = 0.3333;
    private RandomGenerator(){}

    public static LinkedList<Pawn> generatePawns(String color){
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

        return  generatedPawnList;
    }
}
