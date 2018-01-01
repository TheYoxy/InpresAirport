package Frame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

import NetworkObject.Table;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainController implements Initializable {
    @FXML private TableView<Vector<String>> table;
    private Socket s;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Table vols = null;

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

            this.s = loginController.getSocket();
            this.ois = loginController.getObjectInputStream();
            this.oos = loginController.getObjectOutputStream();
            this.vols = loginController.getVols();
            for (int i = 0; i < vols.getColumnCount(); i++) {
                TableColumn tc = new TableColumn(vols.getTete().elementAt(i));
                final int cell = i;
                tc.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Vector<String>, String>, ObservableValue>) cellDataFeatures -> new SimpleStringProperty(cellDataFeatures.getValue().elementAt(cell)));
                tc.prefWidthProperty().bind(table.widthProperty().divide(vols.getColumnCount()));
                table.getColumns().add(tc);
            }
            ObservableList<Vector<String>> data = FXCollections.observableArrayList();
            data.addAll(vols.getChamps());
            table.setItems(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
