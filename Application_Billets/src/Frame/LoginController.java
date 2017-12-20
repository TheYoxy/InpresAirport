package Frame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import Tools.AsyncTask;
import Tools.PropertiesReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController implements Initializable {
    @FXML private VBox LoginPanel;
    @FXML private Pane ChargementPanel;
    @FXML private ProgressIndicator Chargement;
    @FXML private TextField Username;
    @FXML private PasswordField Password;
    @FXML private Button connexion;
    @FXML private Label Error;
    private boolean connected;
    private Socket s;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public Socket getS() {
        return s;
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connected = false;
        Chargement.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
    }

    @FXML
    private void connexion(ActionEvent actionEvent) {
        if (isInputValid())
            new Connexion().execute();
        else if (Username.getText().equals(""))
            new Alert(Alert.AlertType.ERROR, "Nom d'utilisateur invalide", ButtonType.OK).showAndWait();
        else if (Password.getText().equals(""))
            new Alert(Alert.AlertType.ERROR, "Mot de passe invalide", ButtonType.OK).showAndWait();
    }

    private boolean isInputValid() {
        return !Username.getText().equals("") && !Password.getText().equals("");
    }

    private class Connexion extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ChargementPanel.setVisible(true);
            LoginPanel.setVisible(false);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 0) {
                connected = true;
                ((Stage) connexion.getScene().getWindow()).close();
            } else {
                ChargementPanel.setVisible(false);
                LoginPanel.setVisible(true);
                String message = "";
                switch (result) {
                    case -1:
                        message = "Connexion au serveur impossible (Timeout)";
                        break;
                    case -2:
                        message = "Impossible de résoudre le nom du serveur";
                        break;
                }
                new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                s = new Socket();
                s.connect(new InetSocketAddress(InetAddress.getByName(PropertiesReader.getProperties("ServerName")), Integer.valueOf(PropertiesReader.getProperties("Port"))), 5000);
            } catch (SocketTimeoutException e) {
                return -1;
            } catch (UnknownHostException e) {
                return -2;
            } catch (IOException e) {
                return -3;
            }

            return 0;
        }
    }
}
