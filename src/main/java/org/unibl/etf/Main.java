package org.unibl.etf;

import javafx.application.Application;
import javafx.stage.Stage;
import org.unibl.etf.controllers.ErrorViewController;
import org.unibl.etf.exceptions.ConfigException;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.Util;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;

public class Main extends Application {
    public static final String TITLE = "Java Simulation 2022 - Diamond Circle";
    public static final int MIN_HEIGHT = 720;
    public static final int MIN_WIDTH = 1080;
    public static final int PROGRAM_TITLE_BAR_HEIGHT = 37;
    public static final int PROGRAM_SCROLLBAR_WIDTH = 16;

    public final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage stage) throws IOException {
        try {
            FileHandler handler = new FileHandler("default.log", true);
            LOGGER.addHandler(handler);
            ConfigReader.readConfiguration();
            Util.createWindow("views/main-view.fxml", TITLE, stage, MIN_HEIGHT + PROGRAM_TITLE_BAR_HEIGHT,
                    MIN_WIDTH + PROGRAM_SCROLLBAR_WIDTH, true);
        } catch (ConfigException e) {
            LOGGER.log(Level.INFO, e.toString(), e);
            ((ErrorViewController) Util.createWindow("views/error-view.fxml", TITLE, stage, 0, 0,
                                            false)).errorLbl.setText(e.getMessage());

        }
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}