package Serveur;

import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

import ServeurClientLog.Threads.ThreadClient;
import ServeurClientLog.Threads.ThreadServeur;
import TICKMAP.RequeteTICKMAP;
import Tools.FTextAreaOutputStream;
import Tools.PropertiesReader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
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
        ts = new ThreadServeur(Integer.valueOf(PropertiesReader.getProperties("PORT_BILLETS")), Integer.valueOf(PropertiesReader.getProperties("NB_THREADS")), RequeteTICKMAP.class);

        ThreadClient[] tc = ts.getListChild();
        for (ThreadClient aTc : tc) {
            RadioButton rb = new RadioButton(aTc.getName());
            aTc.addStateChangedListener((state) -> Platform.runLater(() -> rb.setSelected(state)));
            rb.getStyleClass().add("rb");
            VBoxStatus.getChildren().addAll(rb);
        }
        /* Lancement du serveur */
        ts.start();
    }
}
