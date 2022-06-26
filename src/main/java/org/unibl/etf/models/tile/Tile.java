package org.unibl.etf.models.tile;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.unibl.etf.Main;
import org.unibl.etf.models.pawn.Pawn;
import org.unibl.etf.util.ConfigReader;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Tile extends StackPane {
    private static final Image diamondImage = new Image(String.valueOf(Main.class.getResource("img/diamond.png")));
    public AtomicBoolean isOccupied = new AtomicBoolean(false);
    public ImageView imageView = new ImageView();
    private static final String CSS_CLASS = "tile";
    protected GridPane grid;
    public int diamonds = 0;
    public Pawn pawn = null;


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

    public synchronized void setImage(Image image){
        this.isOccupied.set(image != null);
        Platform.runLater(() -> imageView.setImage(image));
    }

    public synchronized void addDiamonds(int diamonds){
        this.diamonds += diamonds;
        if(!isOccupied.get())
            Platform.runLater(() -> imageView.setImage(diamondImage));
    }

    @Override
    public String toString() {
        return String.valueOf(GridPane.getRowIndex(this) * ConfigReader.mapSize +GridPane.getColumnIndex(this));
    }
}
