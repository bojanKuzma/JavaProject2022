package org.unibl.etf.models.pawn;

import org.unibl.etf.Main;
import org.unibl.etf.models.tile.Tile;
import org.unibl.etf.util.ConfigReader;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class GhostPawn extends Pawn implements Runnable {
    private final HashMap<Integer, Tile> map;
    private final AtomicBoolean stopGame;
    private final static int TIME = 5000;

    public GhostPawn(HashMap<Integer, Tile> map, AtomicBoolean stopGame) {
        this.map = map;
        this.stopGame = stopGame;
    }

    @Override
    public void run() {
        Random random = new Random();
        while(true){
            if(!stopGame.get()){
               map.get(random.nextInt(ConfigReader.mapSize * ConfigReader.mapSize)).addDiamonds
                       (
                       random.nextInt(ConfigReader.mapSize + 1) + 2
                       );
            }
            try {
                Thread.sleep(TIME);
            } catch (InterruptedException e) {
                Main.LOGGER.log(Level.INFO, e.toString(), e);
            }
        }
    }
}
