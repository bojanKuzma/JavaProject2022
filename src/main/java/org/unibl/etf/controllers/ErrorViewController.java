package org.unibl.etf.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ErrorViewController {
    @FXML
    public Label errorLbl;

    public void exit() {
        Platform.exit();
    }
}
