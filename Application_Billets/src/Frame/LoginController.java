package Frame;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import NetworkObject.AESParams;
import NetworkObject.CryptedPackage;
import NetworkObject.Login;
import NetworkObject.Table;
import TICKMAP.ReponseTICKMAP;
import TICKMAP.RequeteTICKMAP;
import TICKMAP.TickmapThreadRequest;
import TICKMAP.TypeReponseTICKMAP;
import TICKMAP.TypeRequeteTICKMAP;
import Tools.AsyncTask;
import Tools.DigestCalculator;
import Tools.Procedural;
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
    private Table vols = null;

    public Socket getSocket() {
        return s;
    }

    public ObjectInputStream getObjectInputStream() {
        return ois;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return oos;
    }

    public Table getVols() {
        return vols;
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
        if (isInputValid()) new Connexion().execute();
        else if (Username.getText().equals(""))
            new Alert(Alert.AlertType.ERROR, "Nom d'utilisateur invalide", ButtonType.OK).showAndWait();
        else if (Password.getText().equals(""))
            new Alert(Alert.AlertType.ERROR, "Mot de passe invalide", ButtonType.OK).showAndWait();
    }

    private boolean isInputValid() {
        return !Username.getText().equals("") && !Password.getText().equals("");
    }

    private class Connexion extends AsyncTask<Void, Void, Integer> {
        private static final int SOCKET_TIMEOUT = -1;
        private static final int UNKNOWHOST = -2;
        private static final int IOEXCEPTION = -3;
        private static final int CLASSNOTFOUND = -4;
        private static final int SERVERERROR = -5;
        private static final int BADPARAM = -6;
        private static final int BAD_LOGIN = -10;
        private static final int BAD_PASSWORD = -11;
        private static final int OTHER = 1;
        private static final int OK = 0;
        private String error = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("onPreExecute()");
            ChargementPanel.setVisible(true);
            LoginPanel.setVisible(false);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            System.out.println("onPostExecute()");
            if (result == 0) {
                connected = true;
                ((Stage) connexion.getScene().getWindow()).close();
            } else {
                ChargementPanel.setVisible(false);
                LoginPanel.setVisible(true);
                String message = "";
                switch (result) {
                    case SOCKET_TIMEOUT:
                        message = "Connexion au serveur impossible (Timeout)";
                        message += error;
                        break;
                    case UNKNOWHOST:
                        message = "Impossible de résoudre le nom du serveur\n";
                        message += error;
                        break;
                    case IOEXCEPTION:
                        message = "IOexception: " + error;
                        break;
                    case CLASSNOTFOUND:
                        message = "Class not found: " + error;
                        break;
                    case SERVERERROR:
                        message = "Erreur au niveau du serveur";
                        break;
                    case BADPARAM:
                        message = "Erreur de valeur de retour";
                        break;
                    case BAD_LOGIN:
                        message = "Utilisateur inconnu";
                        break;
                    case BAD_PASSWORD:
                        message = "Mot de passe incorrect";
                        break;
                    case OTHER:
                        message = error;
                        break;
                }
                Alert a = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
                a.showAndWait();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            System.out.println("doInBackground()");
            int challenge;
            try {
                if (s == null) {
                    s = new Socket();
                    s.connect(new InetSocketAddress(InetAddress.getByName(PropertiesReader.getProperties("ServerName")), Integer.valueOf(PropertiesReader.getProperties("Port"))), 5000);
                    ObjectOutputStream tempoos = new ObjectOutputStream(s.getOutputStream());
                    tempoos.writeObject(new TickmapThreadRequest());
                }
                if (oos == null) oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(new RequeteTICKMAP(TypeRequeteTICKMAP.TryConnect, Procedural.IpPort(s)));
                if (ois == null) ois = new ObjectInputStream(s.getInputStream());
                ReponseTICKMAP rep = (ReponseTICKMAP) ois.readObject();
                if (rep.getCode() == TypeReponseTICKMAP.OK) {
                    if (rep.getParam() != null) {
                        challenge = (int) rep.getParam();
                        oos.writeObject(new RequeteTICKMAP(TypeRequeteTICKMAP.Login, new Login(Username.getText(), DigestCalculator.hashPassword(Password.getText(), challenge))));
                        rep = (ReponseTICKMAP) ois.readObject();
                        if (rep.getCode() == TypeReponseTICKMAP.OK)
                            switch ((TypeReponseTICKMAP) rep.getCode()) {
                                case UNKNOWN_LOGIN:
                                    return BAD_LOGIN;
                                case BAD_PASSWORD:
                                    return BAD_PASSWORD;
                                case OK:
                                    KeyExchange();
                                    return OK;
                            }
                        else return SERVERERROR;
                    } else return BADPARAM;
                } else return SERVERERROR;
            } catch (SocketTimeoutException e) {
                error = e.getMessage();
                return SOCKET_TIMEOUT;
            } catch (UnknownHostException e) {
                error = e.getMessage();
                return UNKNOWHOST;
            } catch (IOException e) {
                error = e.getMessage();
                return IOEXCEPTION;
            } catch (ClassNotFoundException e) {
                error = e.getMessage();
                return CLASSNOTFOUND;
            } catch (NoSuchAlgorithmException e) {
                error = e.getLocalizedMessage();
            } catch (CertificateException e) {
                error = e.getLocalizedMessage();
            } catch (KeyStoreException e) {
                error = e.getLocalizedMessage();
            } catch (InvalidKeyException e) {
                error = e.getLocalizedMessage();
            } catch (InvalidAlgorithmParameterException e) {
                error = e.getLocalizedMessage();
            } catch (NoSuchPaddingException e) {
                error = e.getLocalizedMessage();
            } catch (BadPaddingException e) {
                error = e.getLocalizedMessage();
            } catch (NoSuchProviderException e) {
                error = e.getLocalizedMessage();
            } catch (IllegalBlockSizeException e) {
                error = e.getLocalizedMessage();
            }
            return OTHER;
        }

        /**
         * Méthode qui fait l'échange de la clef de session entre le serveur et le client
         *
         * @throws NoSuchProviderException
         * @throws NoSuchAlgorithmException
         * @throws InvalidKeyException
         * @throws CertificateException
         * @throws KeyStoreException
         * @throws NoSuchPaddingException
         * @throws IOException
         * @throws BadPaddingException
         * @throws IllegalBlockSizeException
         * @throws InvalidAlgorithmParameterException
         * @throws ClassNotFoundException
         */
        private void KeyExchange() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, CertificateException, KeyStoreException, NoSuchPaddingException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, ClassNotFoundException {
            ReponseTICKMAP rep;
            do {
                CryptedPackage cp = new CryptedPackage(genSecretAES(), genAESParams());
                Cipher cipher = genPublicKey();
                System.out.println("Création du flux");
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
                System.out.println("TEST");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream tempoos = new ObjectOutputStream(baos);
                tempoos.writeObject(cp);
                System.out.println("tempoos.writeObject(cp);");
                dos.write(cipher.doFinal(baos.toByteArray()));
                System.out.println("Ecriture");
                dos.flush();
                System.out.println("Flush");
                rep = (ReponseTICKMAP) ois.readObject();
            } while (rep.getCode() != TypeReponseTICKMAP.OK);
            vols = (Table) rep.getParam();
        }

        /**
         * @return
         * @throws NoSuchProviderException
         * @throws NoSuchAlgorithmException
         */
        private SecretKey genSecretAES() throws NoSuchProviderException, NoSuchAlgorithmException {
            System.out.println("Génération d'une clef Rijndael/AES");
            KeyGenerator key = KeyGenerator.getInstance("Rijndael", "BC");
            System.out.print(".");
            key.init(new SecureRandom());
            System.out.print(".");
            return key.generateKey();
        }

        /**
         * @return Object AESParams contenant toutes les données pour pouvoir faire de l'AESParams
         * @throws NoSuchProviderException
         * @throws NoSuchAlgorithmException
         */
        private AESParams genAESParams() throws NoSuchProviderException, NoSuchAlgorithmException {
            System.out.print("Génération de la clef de session ");
            SecretKey secretKey = genSecretAES();
            System.out.print(".");
            byte[] init = new byte[16];
            System.out.print(".");
            new SecureRandom().nextBytes(init);
            System.out.println(".");
            return new AESParams(secretKey, init);
        }

        /**
         * @return
         * @throws KeyStoreException
         * @throws IOException
         * @throws NoSuchPaddingException
         * @throws NoSuchAlgorithmException
         * @throws NoSuchProviderException
         * @throws InvalidKeyException
         * @throws CertificateException
         */
        private Cipher genPublicKey() throws KeyStoreException, IOException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, CertificateException {
            System.out.println("Récupération de la clef publique du serveur");
            KeyStore ks = KeyStore.getInstance("jks");
            ks.load(new FileInputStream(System.getProperty("user.home") + "/.keystore"), "GR;Ps~\"[?3])N9j4".toCharArray());
            X509Certificate certificate = (X509Certificate) ks.getCertificate("appbillets");
            PublicKey pk = certificate.getPublicKey();
            Cipher cipher = Cipher.getInstance(pk.getAlgorithm(), "BC");
            cipher.init(Cipher.PUBLIC_KEY, pk);
            return cipher;
        }
    }
}
