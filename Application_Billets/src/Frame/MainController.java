package Frame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import NetworkObject.Bean.Voyageur;
import NetworkObject.Places;
import NetworkObject.Table;
import TICKMAP.ReponseTICKMAP;
import TICKMAP.RequeteTICKMAP;
import TICKMAP.TypeReponseTICKMAP;
import TICKMAP.TypeRequeteTICKMAP;
import Tools.AESCryptedSocket;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainController implements Initializable {
    @FXML
    private TableView<Vector<String>> table;
    private Socket s;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Table vols;
    private AESCryptedSocket cryptedSocket;

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
            vols = loginController.getVols();
            cryptedSocket = new AESCryptedSocket(this.s, loginController.getCp().getParams());
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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void tableOnMouseClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
            int max = 0;
            Vector<String> selected = table.getSelectionModel().getSelectedItem();
            System.out.println("selected = " + selected);
            int i;
            if (selected == null) return;
            for (i = 0; i < vols.getColumnCount(); i++)
                if (!vols.getChamps().get(i).get(0).equalsIgnoreCase(selected.get(0))) break;

            if (i != vols.getColumnCount()) {
                System.out.println("Vol found");
                int index = -1;
                for (int j = 0; j < vols.getTete().size(); j++)
                    if (vols.getTete().get(j).equalsIgnoreCase("PlacesDisponible")) {
                        index = j;
                        break;
                    }
                if (index != -1) {
                    System.out.println("Max found");
                    max = Integer.parseInt(vols.getChamps().get(i).get(index));
                    System.out.println("Max: " + max);
                }
            }
            try {
                Stage s = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader();
                Parent root = fxmlLoader.load(getClass().getResource("Voyageurs.fxml").openStream());
                VoyageursController voyageursController = fxmlLoader.getController();
                voyageursController.setNbMax(max);
                s.setScene(new Scene(root));
                s.setResizable(false);
                s.initModality(Modality.APPLICATION_MODAL);
                s.centerOnScreen();
                s.showAndWait();

                ObservableList<Voyageur> list = voyageursController.getListVoyageurs();
                oos.writeObject(new RequeteTICKMAP(TypeRequeteTICKMAP.Ajout_Voyageurs));
                List l = new LinkedList<>();
                l.add(selected.get(0));
                l.add(list.toArray(new Voyageur[list.size()]));
                cryptedSocket.writeObject((Serializable) l);

                ReponseTICKMAP rep = (ReponseTICKMAP) ois.readObject();
                if (rep.getCode() == TypeReponseTICKMAP.OK) {
                    System.out.println("OK");
                    Places p = (Places) cryptedSocket.readObject();

                    Dialog dialog = new Alert(Alert.AlertType.CONFIRMATION);
                    dialog.setTitle("");
                    dialog.setHeaderText("Confirmation de la réservation");

                    GridPane grid = new GridPane();
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(20, 150, 10, 10));

                    TextField prix = new TextField();
                    prix.setText(String.valueOf(p.getPrix()));
                    prix.setEditable(false);

                    ListView<String> ids = new ListView();
                    ids.getItems().addAll(p.getNumPlaces());
                    ids.setEditable(false);
                    ids.setPrefHeight(ids.getMinHeight());
                    grid.add(new Label("Prix: "), 0, 0);
                    grid.add(prix, 1, 0);
                    grid.add(new Label(ids.getItems().size() > 1 ? "Numéros de billets: " : "Numéro de billet: "), 0, 1);
                    grid.add(ids, 1, 1);

                    dialog.getDialogPane().setContent(grid);
                    Optional val = dialog.showAndWait();
                    val.ifPresent(o -> {
                        System.out.println(o.getClass().getName());
                    });

                } else if (rep.getCode() == TypeReponseTICKMAP.FULL) {
                    System.out.println("FULL");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}