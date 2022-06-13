package org.unibl.etf.models.pawn;

import javafx.scene.image.Image;

public class FlyingPawn extends Pawn implements Flyable{
    public FlyingPawn(Color color, Image image, int index) {
        super(color, image, index);
    }

    public String toString(){
        return "Leteća figura - " + index;
    }
}
