package org.unibl.etf.models.pawn;

import javafx.scene.image.Image;
import org.unibl.etf.models.tile.Tile;
import org.unibl.etf.util.ConfigReader;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Pawn {
    protected Color color;
    public Image image;
    protected int index;

    public final AtomicBoolean displayed = new AtomicBoolean(false);
    public final AtomicBoolean xReachedMaxBoundary = new AtomicBoolean(false);
    public final AtomicBoolean yReachedMaxBoundary = new AtomicBoolean(false);
    public transient int currentPosition = ConfigReader.mapSize / 2 ;//todo magic numbers
    public int diamonds = 0;
    public int upperBoundary = ConfigReader.mapSize - 1;
    public int lowerBoundary = 0;
    public boolean reachedEnd = false;

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

    public boolean hasBeenTraversed(Tile tile){
        return traversedTiles.contains(tile);
    }

}
