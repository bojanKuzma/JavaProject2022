package org.unibl.etf;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.unibl.etf.controllers.ErrorViewController;
import org.unibl.etf.exceptions.ConfigException;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.Util;

import java.io.IOException;

public class Main extends Application {
    public static final String TITLE = "Java Simulation 2022 - Diamond Circle";
    public static final int MIN_HEIGHT = 720;
    public static final int MIN_WIDTH = 1080;
    public static final int PROGRAM_TITLE_BAR_HEIGHT = 37;
    public static final int PROGRAM_SCROLLBAR_WIDTH = 16;

    @Override
    public void start(Stage stage) throws IOException {
//        Scene scene = null;
        try {
            ConfigReader.readConfiguration();
            Util.createWindow("views/main-view.fxml", TITLE, stage, MIN_HEIGHT + PROGRAM_TITLE_BAR_HEIGHT,
                    MIN_WIDTH + PROGRAM_SCROLLBAR_WIDTH, true);
            //todo obrisi nepotrebno
//            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/main-view.fxml"));
//            scene = new Scene(fxmlLoader.load(), MIN_WIDTH, MIN_HEIGHT);
//            stage.setMinHeight(MIN_HEIGHT + PROGRAM_TITLE_BAR_HEIGHT);
//            stage.setMinWidth(MIN_WIDTH + PROGRAM_SCROLLBAR_WIDTH);

        } catch (ConfigException e) {
            //todo logger
            ((ErrorViewController) Util.createWindow("views/error-view.fxml", TITLE, stage, 0, 0,
                                            false)).errorLbl.setText(e.getMessage());
//            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/error-view.fxml"));
//            scene = new Scene(fxmlLoader.load());
//            stage.resizableProperty().setValue(false);
//            ((ErrorViewController) fxmlLoader.getController()).errorLbl.setText(e.getMessage());
        }
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}