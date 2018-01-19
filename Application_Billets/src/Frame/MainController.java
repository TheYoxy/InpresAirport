package Frame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;

import NetworkObject.Bean.Carte;
import NetworkObject.Bean.MACMessage;
import NetworkObject.Bean.Payement;
import NetworkObject.Bean.Places;
import NetworkObject.Bean.Table;
import NetworkObject.Bean.Voyageur;
import NetworkObject.CryptedPackage;
import Protocole.PAYP.PAYPThreadRequest;
import Protocole.PAYP.ReponsePAYP;
import Protocole.PAYP.RequetePAYP;
import Protocole.PAYP.TypeReponsePAYP;
import Protocole.PAYP.TypeRequetePAYP;
import Protocole.SEBATRAP.TypeReponseST;
import Protocole.TICKMAP.ReponseTICKMAP;
import Protocole.TICKMAP.RequeteTICKMAP;
import Protocole.TICKMAP.TypeReponseTICKMAP;
import Protocole.TICKMAP.TypeRequeteTICKMAP;
import Tools.AESCryptedSocket;
import Tools.Crypto.FonctionsCrypto;
import Tools.PropertiesReader;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

public class MainController implements Initializable {
    private static final String KEYSTORE       = "Application_Billets.pkcs12";
    private static final String KEY_APPBILLETS = "appbillets";
    private static final String KEY_PAYEMENT   = "serveurpayement";
    private static final String STOREPASS      = "azerty";
    private AESCryptedSocket          cryptedSocket;
    private Mac                       hmac;
    private ObjectInputStream         ois;
    private ObjectOutputStream        oos;
    private Socket                    s;
    @FXML
    private TableView<Vector<String>> table;
    private Table                     vols;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Stage           s               = new Stage();
            FXMLLoader      fxmlLoader      = new FXMLLoader();
            Parent          root            = fxmlLoader.load(getClass().getResource("Login.fxml").openStream());
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

            /* Récupération des params de crypto et réseau et initialisation */
            this.s = loginController.getSocket();
            this.ois = loginController.getObjectInputStream();
            this.oos = loginController.getObjectOutputStream();
            vols = loginController.getVols();
            CryptedPackage cp = loginController.getCp();
            cryptedSocket = new AESCryptedSocket(this.s, cp.getParams());

            hmac = Mac.getInstance("HMAC-SHA1", "BC");
            hmac.init(cp.getKey());

            /* Mise en place de la table */
            for (int i = 0; i < vols.getColumnCount(); i++) {
                TableColumn tc   = new TableColumn(vols.getTete().elementAt(i));
                final int   cell = i;
                tc.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Vector<String>, String>, ObservableValue>) cellDataFeatures
                        -> new SimpleStringProperty(cellDataFeatures.getValue().elementAt(cell)));
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
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            switch (mouseEvent.getClickCount()) {
                case 1:
                    break;
                case 2: {
                    int            max      = 0;
                    Vector<String> selected = table.getSelectionModel().getSelectedItem();
                    System.out.println("Selected = " + selected);
                    int i;
                    if (selected == null) return;
                    for (i = 0; i < vols.getColumnCount(); i++)
                        if (!vols.getChamps().get(i).get(0).equalsIgnoreCase(selected.get(0)))
                            break;

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
                        table.getScene().getWindow().hide();
                        Stage               s                   = new Stage();
                        FXMLLoader          fxmlLoader          = new FXMLLoader();
                        Parent              root                = fxmlLoader.load(getClass().getResource("Voyageurs.fxml").openStream());
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
                            Places   p  = (Places) cryptedSocket.readObject();
                            Optional o1 = showConfirm(p);
                            if (o1.isPresent()) {
                                rep = new ReponseTICKMAP(TypeReponseTICKMAP.OK, new MACMessage(hmac, new ReponseTICKMAP(TypeReponseTICKMAP.OK)));
                                oos.writeObject(rep);
                                Optional<Pair<Voyageur, String>> opt = showCardAndName(list);
                                if (opt.isPresent()) {
                                    Pair<Voyageur, String> pair = opt.get();
                                    Voyageur               v    = pair.getKey();
                                    String                 nom  = v.getNom();
                                    String                 card = pair.getValue();

                                    Socket             payement    = new Socket(InetAddress.getByName(PropertiesReader.getProperties("PAYEMENT_NAME")), Integer.parseInt(PropertiesReader.getProperties("PORT_PAYEMENT")));
                                    ObjectOutputStream oosPayement = new ObjectOutputStream(payement.getOutputStream());
                                    oosPayement.writeObject(new PAYPThreadRequest());
                                    oosPayement = new ObjectOutputStream(payement.getOutputStream());
                                    Cipher asym = FonctionsCrypto.loadPublicKey(KEYSTORE, STOREPASS, KEY_PAYEMENT);
                                    //TODO AJOUT SIGNATURE
                                    Carte             c              = new Carte(nom, card);
                                    ObjectInputStream oisPayement    = new ObjectInputStream(payement.getInputStream());
                                    boolean           pay            = true;
                                    RequeteTICKMAP    requeteTICKMAP = null;
                                    while (pay) {
                                        oosPayement.writeObject(new RequetePAYP(TypeRequetePAYP.PAYEMENT, (Serializable) FonctionsCrypto.encrypt(new Payement(c, p.getPrix()), asym)));
                                        ReponsePAYP reponse = (ReponsePAYP) oisPayement.readObject();
                                        System.out.println("Reponse: " + reponse);
                                        System.out.println("Param:   " + reponse.getParam());
                                        System.out.println("Param: (classname) " + reponse.getParam().getClass().getName());
                                        if (reponse.getCode() == TypeReponsePAYP.OK) {
                                            System.out.println("Réponse: " + reponse.getParam());
                                            System.out.println("Réponse (classname): " + reponse.getParam().getClass().getName());
                                            new Alert(Alert.AlertType.INFORMATION, "Réservation completée.\nId de la transaction: " + reponse.getParam()).showAndWait();
                                            requeteTICKMAP = new RequeteTICKMAP(TypeRequeteTICKMAP.Confirm_Payement, new MACMessage(hmac, reponse.getParam()));
                                            pay = false;
                                        }
                                        else if (reponse.getParam() == TypeReponseST.CARD_NOT_FOUND) {
                                            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "La carte n'existe pas.\n Création d'une nouvelle carte ?", ButtonType.YES, ButtonType.NO);
                                            a.showAndWait();
                                            if (a.getResult() == ButtonType.YES) {
                                                Optional<Pair<Carte, Double>> optional = showNewCard(c, list);
                                                if (optional.isPresent()) {
                                                    Pair<Carte, Double> pair1       = optional.get();
                                                    RequetePAYP         requetePAYP = new RequetePAYP(TypeRequetePAYP.NEW_CARD, FonctionsCrypto.encrypt(pair1.getKey(), asym), FonctionsCrypto.encrypt(pair1.getValue(), asym));
                                                    oosPayement.writeObject(requetePAYP);
                                                    reponse = (ReponsePAYP) oisPayement.readObject();
                                                    //TODO Traitement réponse
                                                }
                                            }
                                            else {
                                                requeteTICKMAP = new RequeteTICKMAP(TypeRequeteTICKMAP.Payement_Abort);
                                                pay = false;
                                            }
                                        }
                                        else {
                                            new Alert(Alert.AlertType.INFORMATION, "La réservation n'a pas pu être effectuée. \nCause: " + reponse.getParam()).showAndWait();
                                            //AJOUT DU MAC
                                            requeteTICKMAP = new RequeteTICKMAP(TypeRequeteTICKMAP.Payement_Abort);
                                            pay = false;
                                        }
                                    }
                                    oos.writeObject(requeteTICKMAP);

                                    rep = (ReponseTICKMAP) ois.readObject();
                                    if (rep.getCode() == TypeReponseTICKMAP.OK) {
                                        new Alert(Alert.AlertType.INFORMATION, "Transaction validée").showAndWait();
                                    }
                                    else
                                        new Alert(Alert.AlertType.INFORMATION, "Transaction annulée").showAndWait();
                                }
                            }
                        }
                        else if (rep.getCode() == TypeReponseTICKMAP.FULL) {
                            int   c = (int) rep.getParam();
                            Alert a = new Alert(Alert.AlertType.ERROR);
                            a.setHeaderText(null);
                            a.setContentText("Impossible de réserver les places.\n" +
                                    "Il ne reste que " + c + (c < 1
                                                              ? " place "
                                                              : " places ") + "disponible.");
                            a.showAndWait();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    private static Optional showConfirm(Places p) {
        Dialog dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Confirmation de la réservation");
        dialog.setHeaderText(null);

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
        grid.add(new Label(ids.getItems().size() > 1
                           ? "Numéros de billets: "
                           : "Numéro de billet: "), 0, 1);
        grid.add(ids, 1, 1);

        dialog.getDialogPane().setContent(grid);
        return dialog.showAndWait();
    }

    public static Optional<Pair<Voyageur, String>> showCardAndName(ObservableList<Voyageur> list) {
        // Create the custom dialog.
        Dialog<Pair<Voyageur, String>> dialog = new Dialog<>();
        dialog.setTitle("Données de payement");
        dialog.setHeaderText(null);

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final ListView<Voyageur> proprio = new ListView<>();
        proprio.getItems().addAll(list);
        proprio.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        proprio.setEditable(false);

        final TextField card = new TextField();

        grid.add(new Label("Nom du propriétaire: "), 0, 0);
        grid.add(proprio, 1, 0);
        grid.add(new Label("Numéro de carte:"), 0, 1);
        grid.add(card, 1, 1);

        Node loginButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        loginButton.setDisable(true);

        // Validation
        card.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().length() != 17 || proprio.getSelectionModel().getSelectedItem() == null));
        proprio.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> loginButton.setDisable(newValue == null || card.getText().trim().length() != 17));

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(proprio::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(proprio.getSelectionModel().getSelectedItem(), card.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static Optional<Pair<Carte, Double>> showNewCard(Carte c, ObservableList<Voyageur> list) {
        Dialog<Pair<Carte, Double>> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle carte:");
        dialog.setHeaderText(null);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final ListView<Voyageur> proprio = new ListView<>();
        proprio.getItems().addAll(list);
        proprio.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        proprio.setEditable(false);

        TextField numCarte = new TextField();
        if (c != null) numCarte.setText(c.getNumeroCarte());

        TextField solde = new TextField();
        solde.setText("10000.00");

        grid.add(new Label("Nom du propriétaire: "), 0, 0);
        grid.add(proprio, 1, 0);
        grid.add(new Label("Numéro de carte: "), 0, 1);
        grid.add(numCarte, 1, 1);
        grid.add(new Label("Solde disponible: "), 0, 2);
        grid.add(solde, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(new Carte(proprio.getSelectionModel().getSelectedItem(), numCarte.getText()), Double.parseDouble(solde.getText()));
            }
            return null;
        });
        return dialog.showAndWait();
    }
}