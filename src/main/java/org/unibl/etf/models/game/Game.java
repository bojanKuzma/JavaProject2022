package org.unibl.etf.models.game;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.unibl.etf.models.card.Card;
import org.unibl.etf.models.card.Stoppable;
import org.unibl.etf.models.pawn.Pawn;
import org.unibl.etf.models.pawn.Speedy;
import org.unibl.etf.models.tile.Tile;
import org.unibl.etf.util.ConfigReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Game extends Thread {
    private final int TIME_WAIT = 850;

    private final ArrayList<String> playerNickNames;
    private final transient LinkedList<Pawn> pawns;
    private final HashMap<Integer, Tile> map;
    private final LinkedList<Card> cardDeck;
    private final Label turnDescriptionLbl;
    private final ImageView cardImg;
    private final Label cardLbl;
    private final AtomicBoolean stopGame;
    private final LinkedList<Integer> turnOrder;

    public Game(ArrayList<String> playerNickNames, LinkedList<Pawn> pawns, AtomicBoolean stopGame, LinkedList<Integer> turnOrder,
                HashMap<Integer, Tile> map, LinkedList<Card> cardDeck, ImageView cardImg, Label cardLbl,
                Label turnDescriptionLbl){
        this.playerNickNames = playerNickNames;
        this.pawns = pawns;
        this.turnOrder = turnOrder;
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
        int counter = 0;
        while(pawns.size()>0){
            if((!stopGame.get())) {
                Card card = cardDeck.remove();
                Platform.runLater(() -> {
                    cardImg.setImage(card.getImage());
                    cardLbl.setText(card.toString());
                });
                Pawn pawn = pawns.remove();
                String playerName = playerNickNames.get(turnOrder.get(counter % ConfigReader.numOfPlayers)); //todo get player name by id for example playerColorYELLOW

                if (card instanceof Stoppable) {
                    Platform.runLater(() -> {
                        turnDescriptionLbl.setText("Na potezu je " + playerName + ", izvučena je specijalna karta," +
                                " pojavice se rupe na " + card.getNumber() + " polja, sve figure koje ne lete propast će");
                    });
                    //todo kreiraj rupe
                    try {
                        Thread.sleep(TIME_WAIT);
                    } catch (InterruptedException e) {
                        //todo logger
                        e.printStackTrace();
                    }


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
                        try {
                            Thread.sleep(TIME_WAIT);
                        } catch (InterruptedException e) {
                            //todo logger
                            e.printStackTrace();
                        }
                        //check if pawn was displayed on map
                        if(pawn.displayed.get())
                            nextPosition = findNext(pawn);
                        else
                            nextPosition = pawn.currentPosition;

                        while (map.get(nextPosition).isOccupied.get() || pawn.hasBeenTraversed(map.get(nextPosition))) {
                            pawn.currentPosition = nextPosition;
                            fieldsTraversed++;
                            pawn.addTile(map.get(nextPosition));
                            nextPosition = findNext(pawn);
                        }

                        Tile tile = map.get(nextPosition);

                        if(!pawn.displayed.get()){
                            //check pause flag
                            if(!stopGame.get()) {
                                tile.setImage(pawn.image);
                                pawn.displayed.set(true);
                            }
                        }
                        else {
                            //check pause flag
                            if (!stopGame.get()) {
                                map.get(previousPosition).setImage(null);
                                tile.setImage(pawn.image);
                            }
                        }


                        pawn.currentPosition = nextPosition;
                        previousPosition = nextPosition;
                        pawn.diamonds += tile.diamonds;
                        pawn.addTile(tile);
                        fieldsTraversed++;

                        //after one tile or more moved just sleep for 1 sec
                        try {
                            Thread.sleep(TIME_WAIT);
                        } catch (InterruptedException e) {
                            //todo logger
                            e.printStackTrace();
                        }

                        if(checkIfEnd(pawn)) {
                            pawn.reachedEnd = true;
                            tile.setImage(null);
                            break;
                        }

                    }
                }
                //reset diamonds
                pawn.diamonds = 0;

                if(!checkIfEnd(pawn))
                    pawns.add(pawn);
                cardDeck.add(card);
                counter++;

                }
        }
        System.out.println("Gotovo");
        //todo kraj igre zapocni novu?
    }
    //todo magic numbers

    private int getTilesToTraverse(Card c, Pawn p){
        return p instanceof Speedy ? c.getNumber() * 2 : c.getNumber();
    }

    private int findNext(Pawn p){
        int x = p.currentPosition / ConfigReader.mapSize;
        int y = p.currentPosition % ConfigReader.mapSize;

        //final step in even matrix size
        if (p.upperBoundary - p.lowerBoundary == 1)//todo magic number
            p.yReachedMaxBoundary.set(true);



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
        boolean check = false;
        LinkedList<Tile> tiles = new LinkedList<>();
        tiles.add(map.get(p.currentPosition-ConfigReader.mapSize-1));
        tiles.add(map.get(p.currentPosition-ConfigReader.mapSize+1));
        tiles.add(map.get(p.currentPosition+ConfigReader.mapSize-1));
        tiles.add(map.get(p.currentPosition+ConfigReader.mapSize+1));
        tiles.add(map.get(p.currentPosition+1));
        int counter = 0;
        for (Tile t : tiles) {
            if (p.hasBeenTraversed(t))
                counter++;
        }
        if (counter == 5)//todo magic number
            check = true;

        return check;
    }



}
