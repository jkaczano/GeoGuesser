package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;

public class ResultController implements Initializable {

    @FXML
    Label resultLabel;
    @FXML
    Button closeButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resultLabel.setText(String.format("%.2f",Controller.gameResult));
    }
    @FXML
    private void exitButtonOnAction(ActionEvent event){
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        Controller.counter=-1;
        Controller.gameActive=true;
    }
}
