package org.unibl.etf.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.unibl.etf.Main;
import org.unibl.etf.models.card.Card;
import org.unibl.etf.models.game.Game;
import org.unibl.etf.models.pawn.GhostPawn;
import org.unibl.etf.models.pawn.Pawn;
import org.unibl.etf.models.pawn.Player;
import org.unibl.etf.models.tile.StandardTile;
import org.unibl.etf.models.tile.Tile;
import org.unibl.etf.models.timer.Timer;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.RandomGenerator;
import org.unibl.etf.util.Util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
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

    public static final LinkedList<Card> cardDeck = new LinkedList<>();

    public LinkedList<Integer> turnOrder = new LinkedList<>();




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            numberOfGamesLbl.setText(numberOfGamesLbl.getText() + " " +
                    Files.list(Paths.get("results"))
                    .filter(p -> p.toFile().isFile())
                    .count());
        } catch (IOException e) {
            Main.LOGGER.log(Level.WARNING, e.toString(), e);
        }
        Game.numberOfGamesLbl = numberOfGamesLbl;

        //initializing grid
        Util.removeGridConstraints(grid.getRowCount() - ConfigReader.mapSize, grid.getRowConstraints());
        Util.removeGridConstraints(grid.getColumnCount() - ConfigReader.mapSize, grid.getColumnConstraints());

        //adding stack panes to every cell with image view in stack pane
        for(int i = 0; i< ConfigReader.mapSize; i++) {
            for (int j = 0; j < ConfigReader.mapSize; j++) {
                StandardTile tile = new StandardTile(grid);
                addToGrid(i, j, tile);
                Tooltip tooltip = new Tooltip("" + (i * ConfigReader.mapSize + j));
                Tooltip.install(tile, tooltip);
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

        int orderOfPlayers = 0;
        while(orderOfPlayers < ConfigReader.numOfPlayers){
            turnOrder.add(orderOfPlayers);
            orderOfPlayers++;
        }
        //randomize the turn order list
        Collections.shuffle(turnOrder);



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
            allPawns.add(RandomGenerator.generatePawns(label.getId(), label.getText()));

        //ordering players per turn order
        LinkedList<Player> tempPlayers = new LinkedList<>();
        for(Integer index : turnOrder){
            tempPlayers.add(Game.players.get(index));
        }

        Game.players = tempPlayers;

        LinkedList<Pawn> reshuffledPawns = new LinkedList<>();
        LinkedList<Integer> turnOrderTemp = new LinkedList<>(turnOrder);

        //shuffling player turn order
        int counter = 0;
        while(counter != ConfigReader.numOfPlayers){
            int order = turnOrderTemp.remove();
            LinkedList<Pawn> temp = allPawns.get(order);


            if(!temp.isEmpty()) {
                reshuffledPawns.add(temp.remove());
                turnOrderTemp.add(order);
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

        //creating deck of cards
        cardDeck.addAll(RandomGenerator.generateDeck());
    }



    private void addToGrid(int x, int y, Tile tile){
        grid.add(tile, y, x);
        map.put(x * ConfigReader.mapSize + y, tile);
    }

    public void openPawnModal(MouseEvent mouseEvent) {
        mouseEvent.consume();
        if (pawnList.getSelectionModel().getSelectedItem() != null) {
            Stage stage = new Stage();
            stage.initOwner(grid.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            try {
                ModalController controller = (ModalController) Util.createWindow("views/modal-view.fxml"
                        , Main.TITLE + ' ' + pawnList.getSelectionModel().getSelectedItem()
                        , stage, Main.MIN_HEIGHT + Main.PROGRAM_TITLE_BAR_HEIGHT,
                        Main.MIN_WIDTH + Main.PROGRAM_SCROLLBAR_WIDTH,
                        true);
                controller.showMovement(pawnList.getSelectionModel().getSelectedItem());
                stage.show();
            } catch (IOException e) {
                Main.LOGGER.log(Level.INFO, e.toString(), e);
            }
        }

        //deselect field
        pawnList.getSelectionModel().select(-1);
    }

    public synchronized void startPauseGame(ActionEvent actionEvent) {
        actionEvent.consume();
        if(firstTimeStarted){
            //timer on the right menu
            Timer timer = new Timer(stopGame, timerLbl);
            timer.start();
            Game game = new Game(stopGame, map, cardDeck, cardImg, cardLbl, cardDescriptionLbl);
            game.start();
            GhostPawn ghostPawn = new GhostPawn(map, stopGame);
            Thread thread = new Thread(ghostPawn);
            thread.setDaemon(true);
            thread.start();
            //set to prevent starting already started threads
            firstTimeStarted = false;
        }

        //start/pause game
        stopGame.getAndSet(!stopGame.get());
    }
}