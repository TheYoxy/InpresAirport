package ServeurBillets;

import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

import Protocole.TICKMAP.TickmapThreadRequest;
import ServeurClientLog.Threads.ThreadServeur;
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

    private ThreadServeur ts = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.setOut(new PrintStream(new FTextAreaOutputStream(Console)));

        ts = new ThreadServeur(Integer.valueOf(PropertiesReader.getProperties("PORT_BILLETS")),
                Integer.valueOf(PropertiesReader.getProperties("NB_THREADS")), TickmapThreadRequest.class);

        Procedural.ajoutBoutonListener(ts.getListChild(), VBoxStatus);

        /* Lancement du serveur */
        ts.start();
    }
}
