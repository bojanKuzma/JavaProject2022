package org.unibl.etf.util;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.ConstraintsBase;
import javafx.stage.Stage;
import org.unibl.etf.Main;

import java.io.IOException;

public class Util {
    private Util(){}

    public static Object createWindow(String URI, String title, Stage stage, int minHeight, int minWidth,
                                          boolean resizable) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(URI));
        Scene scene;
        if(minHeight > 0 && minWidth > 0) {
            scene = new Scene(fxmlLoader.load(), minWidth, minHeight);
            stage.setMinHeight(minHeight);
            stage.setMinWidth(minWidth);
        }
        else
            scene = new Scene(fxmlLoader.load());

        if (title != null)
            stage.setTitle(title);

        stage.resizableProperty().setValue(resizable);
        stage.setScene(scene);
        return fxmlLoader.getController();
    }

    public static void removeGridConstraints(int difference, ObservableList<? extends ConstraintsBase> constraints){
        while(difference > 0){
            constraints.remove(0);
            difference--;
        }
    }
}
