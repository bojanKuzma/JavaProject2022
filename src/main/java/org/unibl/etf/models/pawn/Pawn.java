package org.unibl.etf.models.pawn;

import javafx.scene.image.Image;
import org.unibl.etf.models.tile.Tile;

import java.util.LinkedList;

public abstract class Pawn {
    protected Color color;
    protected Image image;
    protected int index;
    protected boolean reachedEnd = false;
    protected LinkedList<Tile> traversedTiles = new LinkedList<>();

    //needed for ghost pawn
    public Pawn(){}

    public Pawn(Color color, Image image, int index){
        this.image = image;
        this.color = color;
        this.index = index;
    }

    public Color getColor(){
        return color;
    }

    public void addTile(Tile tile){
        traversedTiles.add(tile);
    }

}
