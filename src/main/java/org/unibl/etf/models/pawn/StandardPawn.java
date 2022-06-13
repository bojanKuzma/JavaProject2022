package org.unibl.etf.models.pawn;

import javafx.scene.image.Image;

public class StandardPawn extends Pawn{
    public StandardPawn(Color color, Image image, int index){
        super(color, image, index);
    }

    public String toString(){
        return "Obična figura - " + index;
    }
}
