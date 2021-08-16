package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    protected Label TestLabel;

    public void changeLabel(String newText) {
        TestLabel.setText(newText);
    }
    
}
