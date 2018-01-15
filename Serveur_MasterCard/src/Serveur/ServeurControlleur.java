package Serveur;

import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

import Protocole.SEBATRAP.STThreadRequest;
import ServeurClientLog.Threads.SSLThreadServeur;
import Tools.Affichage.FTextAreaOutputStream;
import Tools.Procedural;
import Tools.PropertiesReader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class ServeurControlleur implements Initializable {
    @FXML
    private TextArea Console;
    @FXML
    private VBox VBoxStatus;
    private SSLThreadServeur ts;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.setOut(new PrintStream(new FTextAreaOutputStream(Console)));
        ts = new SSLThreadServeur(Integer.valueOf(PropertiesReader.getProperties("PORT_PAYEMENT")),
                Integer.valueOf(PropertiesReader.getProperties("NB_THREADS")), "Serveur_Billets.jks", "azerty", STThreadRequest.class);

        Procedural.ajoutBoutonListener(ts.getListChild(), VBoxStatus);

        ts.start();
    }
}
