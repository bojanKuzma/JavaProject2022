package org.unibl.etf.models.game;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.unibl.etf.Main;
import org.unibl.etf.models.card.Card;
import org.unibl.etf.models.card.Stoppable;
import org.unibl.etf.models.pawn.Flyable;
import org.unibl.etf.models.pawn.Pawn;
import org.unibl.etf.models.pawn.Player;
import org.unibl.etf.models.pawn.Speedy;
import org.unibl.etf.models.tile.Tile;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.RandomGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class Game extends Thread {
    public static transient LinkedList<Player> players = new LinkedList<>();
    private static final int END_FIELD = 5;
    private static final int TIME_WAIT = 345;
    private static final int TIME_WAIT_SPECIAL = 2000;
    private static final String CSS_SELECTED = "selected";
    private static final String CSS_HOLE = "hole";
    private final transient LinkedList<Pawn> pawns;
    private final HashMap<Integer, Tile> map;
    private final LinkedList<Card> cardDeck;
    private final Label turnDescriptionLbl;
    private final ImageView cardImg;
    private final Label cardLbl;
    private final AtomicBoolean stopGame;




    public Game(LinkedList<Pawn> pawns, AtomicBoolean stopGame, HashMap<Integer, Tile> map, LinkedList<Card> cardDeck,
                ImageView cardImg, Label cardLbl, Label turnDescriptionLbl){
        this.pawns = pawns;
        this.map = map;
        this.cardDeck = cardDeck;
        this.cardImg = cardImg;
        this.cardLbl = cardLbl;
        this.turnDescriptionLbl = turnDescriptionLbl;
        this.stopGame = stopGame;
        setDaemon(true);
    }

    @Override
    public void run(){
        //initialization of temporary player list and pawn list
        LinkedList<Player> tempPlayers = players;
        LinkedList<Pawn> tempPawns = pawns;

        while(tempPlayers.size()>0){
            if((!stopGame.get())) {
                //drawing card from deck
                Card card = cardDeck.remove();
                Platform.runLater(() -> {
                    cardImg.setImage(card.getImage());
                    cardLbl.setText(card.toString());
                });

                //flag if special card is drawn
                boolean dropped = false;

                //check if player has lost all his pawns and to prevent NPE
                while(tempPlayers.getFirst().pawns.size() <= 0)
                    tempPlayers.remove();

                //current player and his pawn
                Player currentPlayer = tempPlayers.getFirst();
                String playerName = currentPlayer.name;
                Pawn pawn = currentPlayer.pawns.remove();


                if (card instanceof Stoppable) {
                    Platform.runLater(() -> {
                        turnDescriptionLbl.setText("Na potezu je " + playerName + ", izvučena je specijalna karta," +
                                " pojavice se rupe na " + card.getNumber() + " polja, sve figure koje ne lete propast će");
                    });
                    ArrayList<Integer> holes = RandomGenerator.getFields(card);

                    //check pause flag
                    while(stopGame.get());

                    for(Integer hole : holes) {
                        Tile tile = map.get(hole);
                        tile.setId(CSS_HOLE);
                        if(tile.pawn != null && !(tile.pawn instanceof Flyable)) {

                            tile.setImage(null);

                            //check for flag at the end of turn
                            if(tile.pawn.equals(pawn))
                                dropped = true;
                            else
                                for(Player p : tempPlayers)
                                    p.pawns.remove(tile.pawn);
                        }

                    }

                    try {
                        Thread.sleep(TIME_WAIT_SPECIAL);
                    } catch (InterruptedException e) {
                        Main.LOGGER.log(Level.WARNING, e.toString(), e);
                    }

                    //check pause flag
                    while(stopGame.get());

                    //end of special card
                    for(Integer hole : holes)
                        map.get(hole).setId(null);
                }
                else {
                    //number of fields to move in the beginning
                    int tilesToMove = getTilesToTraverse(card, pawn);

                    //display text on bottom
                    Platform.runLater(() -> {
                        turnDescriptionLbl.setText("Na potezu je " + playerName + ", " + pawn + ", prelazi "
                                + tilesToMove + " polja, pomjera se sa pozicije " + pawn.currentPosition
                                + " na poziciju " + (pawn.currentPosition + tilesToMove));
                    });


                    //start of traversing
                    int nextPosition;
                    int fieldsTraversed = 0;
                    int previousPosition = pawn.currentPosition;

                    while( (tilesToMove + pawn.diamonds - fieldsTraversed) > 0){

                        //check if pawn was displayed on map
                        if(pawn.displayed.get()) {
                            // set the css of the tile as selected
                            map.get(pawn.currentPosition).setId(CSS_SELECTED);
                            nextPosition = findNext(pawn);
                        }
                        else {
                            // first check if the start position displayed no need to set the css because
                            // the pawn starts off map
                            pawn.addTile(map.get(pawn.currentPosition));
                            if(!map.get(pawn.currentPosition).isOccupied.get())
                                map.get(pawn.currentPosition).setId(CSS_SELECTED);
                            nextPosition = pawn.currentPosition;
                        }


                        while (map.get(nextPosition).isOccupied.get() || pawn.hasBeenTraversed(map.get(nextPosition)) && pawn.currentPosition!=nextPosition) {
                            pawn.currentPosition = nextPosition;
                            fieldsTraversed++;
                            pawn.addTile(map.get(nextPosition));
                            nextPosition = findNext(pawn);
                        }

                        Tile tile = map.get(nextPosition);

                        // set the next tile css so the players sees that the selected pawn will move to there or
                        // if no pawn is selected it starts off map
                        tile.setId(CSS_SELECTED);

                        try {
                            Thread.sleep(TIME_WAIT);
                        } catch (InterruptedException e) {
                            Main.LOGGER.log(Level.WARNING, e.toString(), e);
                        }

                        //check pause flag
                        while(stopGame.get());

                        if(!pawn.displayed.get()){
                            tile.setImage(pawn.image);
                            pawn.displayed.set(true);
                        }
                        else {
                            map.get(previousPosition).setImage(null);
                            tile.setImage(pawn.image);
                        }

                        try {
                            Thread.sleep(TIME_WAIT);
                        } catch (InterruptedException e) {
                            Main.LOGGER.log(Level.WARNING, e.toString(), e);
                        }

                        map.get(previousPosition).setId(null);
                        map.get(previousPosition).pawn = null;

                        tile.pawn = pawn;
                        pawn.currentPosition = nextPosition;
                        previousPosition = nextPosition;
                        pawn.diamonds += tile.diamonds;
                        pawn.addTile(tile);
                        fieldsTraversed++;

                        //after one tile or more moved just sleep for 1 sec
                        try {
                            Thread.sleep(TIME_WAIT);
                        } catch (InterruptedException e) {
                            Main.LOGGER.log(Level.WARNING, e.toString(), e);
                        }

                        if(checkIfEnd(pawn)) {
                            pawn.reachedEnd = true;
                            while(stopGame.get());
                            tile.setImage(null);
                            break;
                        }

                    }
                    map.get(pawn.currentPosition).setId(null);
                }

                //reset diamonds
                pawn.diamonds = 0;

                if(!checkIfEnd(pawn) && !dropped) {
                    currentPlayer.pawns.add(pawn);
                }

                //switch player from front to back
                currentPlayer = tempPlayers.remove();

                //check if the player has more pawns left and if he has move him to back
                if(currentPlayer.pawns.size() > 0)
                    tempPlayers.add(currentPlayer);

                cardDeck.add(card);
                }
        }
        System.out.println("Gotovo");
    }

    private int getTilesToTraverse(Card c, Pawn p){
        return p instanceof Speedy ? c.getNumber() * 2 : c.getNumber();
    }

    private int findNext(Pawn p){
        int x = p.currentPosition / ConfigReader.mapSize;
        int y = p.currentPosition % ConfigReader.mapSize;

        if(p.upperBoundary == x)
            p.xReachedMaxBoundary.set(true);

        if(p.lowerBoundary == x)
            p.xReachedMaxBoundary.set(false);

        if(p.upperBoundary == y)
            p.yReachedMaxBoundary.set(true);

        if(p.lowerBoundary == y)
            p.yReachedMaxBoundary.set(false);


        if(p.xReachedMaxBoundary.get())
            x--;
        else
            x++;

        if(p.yReachedMaxBoundary.get())
            y--;
        else
            y++;


        int nextField = x * ConfigReader.mapSize + y;
        if (p.hasBeenTraversed(map.get(nextField))) {
            nextField = p.currentPosition + 1;
            p.upperBoundary--;
            p.lowerBoundary++;
        }

        return nextField;
    }

    private boolean checkIfEnd(Pawn p){
        LinkedList<Tile> tiles = new LinkedList<>();
        tiles.add(map.get(p.currentPosition - ConfigReader.mapSize - 1));
        tiles.add(map.get(p.currentPosition - ConfigReader.mapSize + 1));
        tiles.add(map.get(p.currentPosition + ConfigReader.mapSize - 1));
        tiles.add(map.get(p.currentPosition + ConfigReader.mapSize + 1));
        tiles.add(map.get(p.currentPosition + 1));

        int counter = 0;

        for (Tile t : tiles) {
            if (p.hasBeenTraversed(t))
                counter++;
        }

        return counter == END_FIELD;
    }



}
