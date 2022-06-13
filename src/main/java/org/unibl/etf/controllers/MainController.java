package org.unibl.etf.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ConstraintsBase;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.unibl.etf.Main;
import org.unibl.etf.models.pawn.Pawn;
import org.unibl.etf.models.tile.StandardTile;
import org.unibl.etf.models.tile.Tile;
import org.unibl.etf.models.timer.Timer;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.RandomGenerator;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    //Top menu
    @FXML
    public Label numberOfGamesLbl;


    private static final AtomicBoolean stopGame = new AtomicBoolean(true);

    boolean firstTimeStarted = true;



    @FXML
    public HBox playerNameHBox;
    @FXML
    public Label playerOneLbl;
    @FXML
    public Label playerTwoLbl;
    @FXML
    public Label playerThreeLbl;
    @FXML
    public Label playerFourLbl;

    //Left menu
    @FXML
    public ListView<Pawn> pawnList;

    //Right menu
    @FXML
    public ImageView cardImg;
    @FXML
    public Label cardLbl;
    @FXML
    public Label timerLbl;
    @FXML
    public Button resultListBtn;

    //Center menu
    @FXML
    public GridPane grid;

    //Bottom menu
    @FXML
    public Label cardDescriptionLbl;


    private final HashMap<Integer,Tile> map = new HashMap<>();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //initializing grid
        removeGridConstraints(grid.getRowCount() - ConfigReader.mapSize, grid.getRowConstraints());
        removeGridConstraints(grid.getColumnCount() - ConfigReader.mapSize, grid.getColumnConstraints());

        //adding stack panes to every cell with image view in stack pane
        for(int i = 0; i< ConfigReader.mapSize; i++) {
            for (int j = 0; j < ConfigReader.mapSize; j++) {
                StandardTile roadTile = new StandardTile(grid);
                addToGrid(i, j, roadTile);
            }
        }


        //initializing player name bar
        int numOfPlayersToRemove = (ConfigReader.MAX_PLAYERS - ConfigReader.numOfPlayers);
        while(numOfPlayersToRemove > 0) {
            //removing last pane
            playerNameHBox.getChildren().remove(playerNameHBox.getChildren().size() - 1);
            //removing last player label
            playerNameHBox.getChildren().remove(playerNameHBox.getChildren().size() - 1);
            numOfPlayersToRemove--;
        }

        //getting turn order of players
        LinkedList<Integer> turnOrder = new LinkedList<>();
        int orderOfPlayers = 0;
        while(orderOfPlayers < ConfigReader.numOfPlayers){
            turnOrder.add(orderOfPlayers);
            orderOfPlayers++;
        }
        //randomize the turn order list
        Collections.shuffle(turnOrder);

        //todo remove
        map.get(48).setImage(new Image(String.valueOf(Main.class.getResource("img/bear/YELLOW.png"))));
        map.get(47).setImage(new Image(String.valueOf(Main.class.getResource("img/bear/RED.png"))));
        map.get(46).setImage(new Image(String.valueOf(Main.class.getResource("img/bear/BLUE.png"))));
        map.get(45).setImage(new Image(String.valueOf(Main.class.getResource("img/bear/GREEN.png"))));
        map.get(8).setImage(new Image(String.valueOf(Main.class.getResource("img/horse/YELLOW.png"))));
        map.get(7).setImage(new Image(String.valueOf(Main.class.getResource("img/horse/RED.png"))));
        map.get(6).setImage(new Image(String.valueOf(Main.class.getResource("img/horse/BLUE.png"))));
        map.get(5).setImage(new Image(String.valueOf(Main.class.getResource("img/horse/GREEN.png"))));
        map.get(18).setImage(new Image(String.valueOf(Main.class.getResource("img/eagle/YELLOW.png"))));
        map.get(17).setImage(new Image(String.valueOf(Main.class.getResource("img/eagle/RED.png"))));
        map.get(16).setImage(new Image(String.valueOf(Main.class.getResource("img/eagle/BLUE.png"))));
        map.get(15).setImage(new Image(String.valueOf(Main.class.getResource("img/eagle/GREEN.png"))));
        //todo remove end

        //getting all labels that need to be set
        List<Label> playerLabels = playerNameHBox.getChildren().stream()
                .filter(Label.class::isInstance)
                .map(Label.class::cast)
                .collect(Collectors.toList());

        //setting labels
        playerLabels.forEach(label -> label.setText(ConfigReader.playerNames.get(playerLabels.indexOf(label))));

        //creating random pawns for players
        LinkedList<LinkedList<Pawn>> allPawns = new LinkedList<>();
        for(Label label : playerLabels)
            allPawns.add(RandomGenerator.generatePawns(label.getId()));

        LinkedList<Pawn> reshuffledPawns = new LinkedList<>();

        //shuffling player turn order
        int counter = 0;
        while(counter != ConfigReader.numOfPlayers){
            int order = turnOrder.remove();
            LinkedList<Pawn> temp = allPawns.get(order);


            if(!temp.isEmpty()) {
                reshuffledPawns.add(temp.remove());
                turnOrder.add(order);
            }
            else
                counter++;
        }

        //setting cell design left menu
        pawnList.setCellFactory(new Callback<>() {
            public ListCell<Pawn> call(ListView<Pawn> param) {
                return new ListCell<>() {
                    @Override
                    public void updateItem(Pawn item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty || item != null ) {
                            setTextFill(Color.web(item.getColor().getColorCode()));
                            setText(item.toString());
                        }
                    }
                };

            }
        });

        //initializing left menu
        pawnList.setItems(FXCollections.observableArrayList(reshuffledPawns));





    }

    private void removeGridConstraints(int difference, ObservableList<? extends ConstraintsBase> constraints){
        while(difference > 0){
            constraints.remove(0);
            difference--;
        }
    }

    private void addToGrid(int x, int y, Tile tile){
        grid.add(tile, y, x);
        map.put(x * ConfigReader.mapSize + y, tile);
    }

    public void openPawnModal(MouseEvent mouseEvent) {
        mouseEvent.consume();
        if (pawnList.getSelectionModel().getSelectedItem() != null)
            System.out.println(pawnList.getSelectionModel().getSelectedItem());
            //todo pozovi novi prozor i pokazi ga

        //deselect field
        pawnList.getSelectionModel().select(-1);//todo magic number
    }

    public synchronized void startPauseGame(ActionEvent actionEvent) {
        actionEvent.consume();
        if(firstTimeStarted){
            Timer timer = new Timer(stopGame, timerLbl);
            timer.start();
            //set to prevent starting already started threads
            firstTimeStarted = false;
        }
        //start/pause game
        stopGame.getAndSet(!stopGame.get());
    }
}