package org.unibl.etf.models.game;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.unibl.etf.Main;
import org.unibl.etf.models.card.Card;
import org.unibl.etf.models.card.Stoppable;
import org.unibl.etf.models.pawn.Flyable;
import org.unibl.etf.models.pawn.Pawn;
import org.unibl.etf.models.pawn.Player;
import org.unibl.etf.models.pawn.Speedy;
import org.unibl.etf.models.tile.Tile;
import org.unibl.etf.models.timer.Timer;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.RandomGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class Game extends Thread {
    public static transient LinkedList<Player> players = new LinkedList<>();
    private static final int END_FIELD = 5;
    private static final int TIME_WAIT = 35;
    private static final int TIME_WAIT_SPECIAL = 100;
    private static final String CSS_SELECTED = "selected";
    private static final String CSS_HOLE = "hole";
    public static Label numberOfGamesLbl;
    private final HashMap<Integer, Tile> map;
    private final LinkedList<Card> cardDeck;
    private final Label turnDescriptionLbl;
    private final ImageView cardImg;
    private final Label cardLbl;
    private final AtomicBoolean stopGame;
    private final HashMap<String, LinkedList<String>> results = new HashMap<>();




    public Game(AtomicBoolean stopGame, HashMap<Integer, Tile> map, LinkedList<Card> cardDeck, ImageView cardImg,
                Label cardLbl, Label turnDescriptionLbl){
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
                                for(Player p : tempPlayers) {
                                    if(p.pawns.remove(tile.pawn))
                                        writeToResults(tile.pawn, p);
                                }
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
                                + " za " + tilesToMove + " polja");
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
                            if(!map.get(pawn.currentPosition).isOccupied.get()) {
                                map.get(pawn.currentPosition).setId(CSS_SELECTED);
                                map.get(pawn.currentPosition).pawn = pawn;
                            }
                            nextPosition = pawn.currentPosition;
                        }

                        int addDiamonds = map.get(nextPosition).diamonds;
                        map.get(nextPosition).diamonds = 0;
                        pawn.diamonds += addDiamonds;
                        if (addDiamonds > 0)
                            Platform.runLater(() -> {
                                turnDescriptionLbl.setText("Na potezu je " + playerName + ", " + pawn + ", prelazi "
                                        + tilesToMove + " polja, pomjera se sa pozicije " + pawn.currentPosition
                                        + " za " + tilesToMove + " polja i ima " + pawn.diamonds + " dijamanata");
                            });



                        while (map.get(nextPosition).isOccupied.get() || pawn.hasBeenTraversed(map.get(nextPosition)) && pawn.currentPosition!=nextPosition) {
                            pawn.currentPosition = nextPosition;
                            fieldsTraversed++;
                            pawn.addTile(map.get(nextPosition));
                            nextPosition = findNext(pawn);
                            addDiamonds = map.get(nextPosition).diamonds;
                            pawn.diamonds += addDiamonds;
                            map.get(nextPosition).diamonds = 0;
                            if (addDiamonds > 0)
                                Platform.runLater(() -> {
                                    turnDescriptionLbl.setText("Na potezu je " + playerName + ", " + pawn + ", prelazi "
                                            + tilesToMove + " polja, pomjera se sa pozicije " + pawn.currentPosition
                                            + " za " + tilesToMove + " polja i ima " + pawn.diamonds + " dijamanata");
                                });
                        }

                        Tile tile = map.get(nextPosition);

                        addDiamonds = map.get(nextPosition).diamonds;
                        pawn.diamonds += addDiamonds;
                        map.get(nextPosition).diamonds = 0;
                        if (addDiamonds > 0)
                            Platform.runLater(() -> {
                                turnDescriptionLbl.setText("Na potezu je " + playerName + ", " + pawn + ", prelazi "
                                        + tilesToMove + " polja, pomjera se sa pozicije " + pawn.currentPosition
                                        + " za " + tilesToMove + " polja i ima " + pawn.diamonds + " dijamanata");
                            });

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
                            map.get(previousPosition).pawn = null;
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
                       // pawn.diamonds += tile.diamonds;
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
                else{
                    writeToResults(pawn, currentPlayer);
                }

                //switch player from front to back
                currentPlayer = tempPlayers.remove();

                //check if the player has more pawns left and if he has move him to back
                if(currentPlayer.pawns.size() > 0)
                    tempPlayers.add(currentPlayer);

                cardDeck.add(card);
                }
        }
        writeResult();
        stopGame.set(true);
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

    private void writeToResults(Pawn p, Player player){
        StringBuilder result = new StringBuilder(p.toString() + " " + p.getColor().toString() + "    - pređeni put (");
        for(Tile t : p.getTraversedTiles())
            result.append(t.toString()).append('-');
        result.append(") - stigla do cilja ").append(p.reachedEnd);
        if(!results.containsKey(player.name))
            results.put(player.name, new LinkedList<>());
        results.get(player.name).add(String.valueOf(result));
    }

    private void writeResult(){
        DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        Date date = new Date();

        Path path = Paths.get("results" + File.separator + "IGRA_" + dateFormat.format(date) + ".txt");
        try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){
            results.forEach((k,v) -> {
                try {
                    writer.write(k);
                    writer.write(System.lineSeparator());

                    for(String result : v) {
                        writer.write(result);
                        writer.write(System.lineSeparator());
                    }

                } catch (IOException e) {
                    Main.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
            });
            writer.write("Ukupno vrijeme trajanja igre: " + Timer.elapsedTimeMinutes + "min " + Timer.elapsedTimeSeconds + "s");
        }catch(IOException ex){
            Main.LOGGER.log(Level.SEVERE, ex.toString(), ex);
        }
        Platform.runLater(() -> {
            try {
                numberOfGamesLbl.setText("Trenutni broj odigranih igara: " +
                        Files.list(Paths.get("results"))
                                .filter(p -> p.toFile().isFile())
                                .count());
                turnDescriptionLbl.setText("Igra je završena");
            } catch (IOException e) {
                Main.LOGGER.log(Level.WARNING, e.toString(), e);
            }
        });
    }


}
