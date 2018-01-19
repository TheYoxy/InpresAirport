package Frame;

import java.net.URL;
import java.util.ResourceBundle;

import NetworkObject.Bean.Voyageur;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class VoyageursController implements Initializable {
    @FXML private MenuBar MenuVoyageur;
    @FXML
    private TextField nom;
    @FXML
    private TextField prenom;
    @FXML
    private DatePicker naissance;
    @FXML
    private GridPane voyageurGrid;
    @FXML
    private ListView<Voyageur> listVoyageurs;
    private int nbMax = Integer.MAX_VALUE;

    @FXML
    private void deleteClicked(ActionEvent actionEvent) {
        listVoyageurs.getItems().removeAll(listVoyageurs.getSelectionModel().getSelectedItems());
        nbMax++;
        if (nbMax > 0 && MenuVoyageur.isDisable()) MenuVoyageur.setDisable(false);
    }

    public ObservableList<Voyageur> getListVoyageurs() {
        return listVoyageurs.getItems();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listVoyageurs.setEditable(false);
    }

    @FXML
    private void nouveauClicked(ActionEvent actionEvent) {
        voyageurGrid.setVisible(true);
        nbMax--;
        if (nbMax == 0)
            MenuVoyageur.setDisable(true);
    }

    public void setNbMax(int nbMax) {
        this.nbMax = nbMax;
        System.out.println("NbMax: " + this.nbMax);
    }

    @FXML
    private void validerClicked(ActionEvent actionEvent) {
        voyageurGrid.setVisible(false);
        listVoyageurs.getItems().add(new Voyageur(nom.getText(), prenom.getText(), naissance.getValue()));
        resetFields();
    }

    private void resetFields() {
        nom.setText("");
        prenom.setText("");
        naissance.setValue(null);
    }
}
