package org.unibl.etf.models.tile;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public abstract class Tile extends StackPane {
    public boolean isOccupied = false;
    public ImageView imageView = new ImageView();
    private static final String CSS_CLASS = "tile";
    protected GridPane grid;


    public Tile(GridPane grid){
        //grid used for binding size of stack pane to cell
        this.grid=grid;

        //StackPane Size
        this.prefHeightProperty().bind(grid.heightProperty().divide(grid.getRowCount()));
        this.prefWidthProperty().bind(grid.widthProperty().divide(grid.getColumnCount()));
        this.getChildren().add(imageView);

        //ImageView Size
        imageView.fitWidthProperty().bind((grid.widthProperty().divide(grid.getColumnCount())));
        imageView.fitHeightProperty().bind(grid.heightProperty().divide(grid.getRowCount()));
        this.setId(CSS_CLASS);
    }

    public void setImage(Image image){
        Platform.runLater(() -> imageView.setImage(image));
    }
}