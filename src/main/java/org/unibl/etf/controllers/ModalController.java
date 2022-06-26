package org.unibl.etf.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import org.unibl.etf.models.game.Game;
import org.unibl.etf.models.pawn.Pawn;
import org.unibl.etf.models.tile.StandardTile;
import org.unibl.etf.models.tile.Tile;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.Util;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ModalController implements Initializable {
    private static final String CSS_SELECTED = "selectedGrid";
    @FXML
    public GridPane grid;
    @FXML
    public Label pawnLbl;

    private HashMap<Integer, StandardTile> map = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //initializing grid
        Util.removeGridConstraints(grid.getRowCount() - ConfigReader.mapSize, grid.getRowConstraints());
        Util.removeGridConstraints(grid.getColumnCount() - ConfigReader.mapSize, grid.getColumnConstraints());

        //adding stack panes to every cell with image view in stack pane
        for(int i = 0; i< ConfigReader.mapSize; i++) {
            for (int j = 0; j < ConfigReader.mapSize; j++) {
                StandardTile tile = new StandardTile(grid);
                grid.add(tile, j, i);
                map.put(i * ConfigReader.mapSize + j, tile);
                Tooltip tooltip = new Tooltip("" + (i * ConfigReader.mapSize + j));
                Tooltip.install(tile, tooltip);
            }
        }
    }

    public void showMovement(Pawn pawn){
        pawnLbl.setText(pawn.toString());
        pawnLbl.getStyleClass().add("playerColor" + pawn.getColor().toString());
        for(Tile t : pawn.getTraversedTiles()) {
            int x = GridPane.getRowIndex(t);
            int y = GridPane.getColumnIndex(t);
            map.get(x * ConfigReader.mapSize + y).setId(CSS_SELECTED);
        }
    }
}
