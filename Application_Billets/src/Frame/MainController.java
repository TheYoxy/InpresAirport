package Frame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Stage s = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Login.fxml").openStream());
            LoginController loginController = fxmlLoader.getController();

            /* Affichage de la frame */
            s.setScene(new Scene(root));
            s.setResizable(false);
            s.initModality(Modality.APPLICATION_MODAL);
            s.centerOnScreen();
            s.showAndWait();
            if (!loginController.isConnected()) {
                Platform.exit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
