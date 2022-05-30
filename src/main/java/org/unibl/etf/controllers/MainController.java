package org.unibl.etf.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ConstraintsBase;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.unibl.etf.models.tile.StandardTile;
import org.unibl.etf.models.tile.Tile;
import org.unibl.etf.util.ConfigReader;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {


    //Top menu
    @FXML
    public Label numberOfGamesLbl;
    @FXML
    public Button startStopBtn;

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
    public VBox pawnList;

    //Right menu
    @FXML
    public ImageView cardImg;
    @FXML
    public Label cardLbl;
    @FXML
    public Button resultListBtn;

    //Center menu
    @FXML
    public GridPane grid;

    //Bottom menu
    @FXML
    public Label cardDescriptionLbl;


    private final HashMap<Integer,Tile> map = new HashMap<>();
    public ListView<String> list;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //initialize grid
        removeGridConstraints(grid.getRowCount() - ConfigReader.numOfRows, grid.getRowConstraints());
        removeGridConstraints(grid.getColumnCount() - ConfigReader.numOfColumns, grid.getColumnConstraints());

        //adding stack panes to every cell with image view in stack pane
        for(int i = 0; i< ConfigReader.numOfRows; i++) {
            for (int j = 0; j < ConfigReader.numOfColumns; j++) {
                StandardTile roadTile = new StandardTile(grid);
                addToGrid(i, j, roadTile);
            }
        }


        //initialize player name bar
        int numOfPlayersToRemove = (ConfigReader.MAX_PLAYERS - ConfigReader.numOfPlayers);
        while(numOfPlayersToRemove > 0) {
            //removing last pane
            playerNameHBox.getChildren().remove(playerNameHBox.getChildren().size() - 1);
            //removing last player label
            playerNameHBox.getChildren().remove(playerNameHBox.getChildren().size() - 1);
            numOfPlayersToRemove--;
        }


        //getting all labels that need to be set
        List<Label> playerLabels = playerNameHBox.getChildren().stream()
                .filter(Label.class::isInstance)
                .map(Label.class::cast)
                .collect(Collectors.toList());

        //setting labels
        playerLabels.forEach(label -> label.setText(ConfigReader.playerNames.get(playerLabels.indexOf(label))));


        //initialize pawns and left menu
        //Set ListView cell factory.
        list.setItems(FXCollections.observableArrayList ("Single", "Double", "Suite", "Family App"));





    }

    private void removeGridConstraints(int difference, ObservableList<? extends ConstraintsBase> constraints){
        while(difference > 0){
            constraints.remove(0);
            difference--;
        }
    }

    private void addToGrid(int x, int y, Tile tile){
        grid.add(tile,y,x,1,1);
        //add to hash map of tiles Key=(i*numberOfRows+j*numberOfColumns) Value=Tile(i,j)
        map.put(x * ConfigReader.numOfRows + y,tile);
    }

    public void openPawnModal(MouseEvent mouseEvent) {
        mouseEvent.consume();
        if (list.getSelectionModel().getSelectedItem() != null)
            System.out.println(list.getSelectionModel().getSelectedItem());

        //deselect field
        list.getSelectionModel().select(-1);//todo magic number
    }
}