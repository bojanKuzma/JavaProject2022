package org.unibl.etf.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import org.unibl.etf.Main;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class FileModalController implements Initializable {
    @FXML
    public ListView<String> listView;

    @FXML
    public TextArea txtArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            //initializing left menu
            listView.setItems(FXCollections.observableArrayList( Files.list(Paths.get("results")).map(Object::toString)
                    .collect(Collectors.toList())));
        } catch (IOException e) {
            Main.LOGGER.log(Level.INFO, e.toString(), e);
        }
    }

    public void openFile(MouseEvent mouseEvent) {
        mouseEvent.consume();
        if (listView.getSelectionModel().getSelectedItem() != null) {
            try {
                txtArea.clear();
                List<String> lines = Files.readAllLines(Path.of(listView.getSelectionModel().getSelectedItem()), StandardCharsets.UTF_8);
                for(String line: lines)
                    Platform.runLater(() ->{
                        txtArea.setText(txtArea.getText() + line + '\n');
                    });
            } catch (IOException e) {
                Main.LOGGER.log(Level.SEVERE, e.toString(), e);
            }
        }
    }
}
