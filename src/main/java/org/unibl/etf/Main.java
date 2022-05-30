package org.unibl.etf;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.unibl.etf.controllers.ErrorViewController;
import org.unibl.etf.exceptions.ConfigException;
import org.unibl.etf.util.ConfigReader;

import java.io.IOException;

public class Main extends Application {
    public static final String TITLE = "Java Simulation 2022 - Diamond Circle";
    private static final int MIN_HEIGHT = 720;
    private static final int MIN_WIDTH = 1080;
    private static final int PROGRAM_TITLE_BAR_HEIGHT = 37;
    private static final int PROGRAM_SCROLLBAR_WIDTH = 16;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            ConfigReader.readConfiguration();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), MIN_WIDTH, MIN_HEIGHT);
            stage.setMinHeight(MIN_HEIGHT + PROGRAM_TITLE_BAR_HEIGHT);
            stage.setMinWidth(MIN_WIDTH + PROGRAM_SCROLLBAR_WIDTH);
            stage.setTitle(TITLE);
            stage.setScene(scene);
            stage.show();
        } catch (ConfigException e) {
            //todo logger
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/error-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.resizableProperty().setValue(false);
            ((ErrorViewController) fxmlLoader.getController()).errorLbl.setText(e.getMessage());
            stage.setTitle(TITLE);
            stage.setScene(scene);
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}