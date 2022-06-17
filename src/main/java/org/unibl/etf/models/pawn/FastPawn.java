package org.unibl.etf.models.pawn;

import javafx.scene.image.Image;

public class FastPawn extends Pawn implements Speedy{
    public FastPawn(Color color, Image image, int index) {
        super(color, image, index);
    }

    public String toString(){
        return "Brza figura - " + index;
    }
}
