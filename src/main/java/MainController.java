

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    protected Label TestLabel;

    public void changeLabel(String newText) {
        TestLabel.setText(newText);
    }

    public void initialize(){
        //Platform.runLater(() -> fetchData());
    }

    private void fetchData(){
        while (true) {
            int data = Data.data;
            TestLabel.setText(Integer.toString(data));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
