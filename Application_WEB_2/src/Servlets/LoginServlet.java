package Servlets;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import NetworkObject.AESParams;
import NetworkObject.Bean.Login;
import NetworkObject.Bean.Table;
import NetworkObject.Bean.WebUser;
import NetworkObject.CryptedPackage;
import Protocole.TICKMAP.ReponseTICKMAP;
import Protocole.TICKMAP.RequeteTICKMAP;
import Protocole.TICKMAP.TickmapThreadRequest;
import Protocole.TICKMAP.TypeReponseTICKMAP;
import Protocole.TICKMAP.TypeRequeteTICKMAP;
import Temp.PropertiesReader;
import Tools.AESCryptedSocket;
import Tools.Crypto.Digest.DigestCalculator;
import Tools.Crypto.FonctionsCrypto;
import Tools.Procedural;

@WebServlet(name = "LoginServlet", value = "/Login")
public class LoginServlet extends HttpServlet {
    private static final String keyname  = "appbillets";
    private static final String keystore = "Application_Web.pkcs12";
    private static final String password = "azerty";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.getSession(false).invalidate();
        String type = request.getParameter("Type");
        try {
            if (type.equals("Log")) {
                String ou = "/error.jsp";
                switch (Log(request, response)) {
                    case 0:
                        ou = "/Vols.jsp";
                        break;
                    case 1:
                    case 2:
                        ou = "/Login.jsp";
                        break;
                    case -1:
                        ou = "/error.jsp";
                        break;
                }
                request.getRequestDispatcher(ou).forward(request, response);
            }
            else if (type.equals("New")) request.getRequestDispatcher(NewUser(request, response)
                                                                      ? "/NewLog.jsp"
                                                                      : "/NLogin.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("From", "Login.doPost 3");
            request.setAttribute("Exception", e);
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    private int Log(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        String      username = request.getParameter("username");
        String      password = request.getParameter("password");
        HttpSession session  = request.getSession(true);
        int         challenge;
        Socket      s        = (Socket) session.getAttribute("s");
        if (s == null) {
            s = new Socket();
            s.connect(new InetSocketAddress(InetAddress.getByName(PropertiesReader.getProperties("BILLETS_NAME")), Integer.valueOf(PropertiesReader.getProperties("PORT_BILLETS"))), 5000);
            session.setAttribute("s", s);

            ObjectOutputStream tempoos = new ObjectOutputStream(s.getOutputStream());
            tempoos.writeObject(new TickmapThreadRequest());
        }
        ObjectOutputStream oos = (ObjectOutputStream) session.getAttribute("oos");
        if (oos == null) oos = new ObjectOutputStream(s.getOutputStream());
        session.setAttribute("oos", oos);

        oos.writeObject(new RequeteTICKMAP(TypeRequeteTICKMAP.TryConnect, Procedural.IpPort(s)));

        ObjectInputStream ois = (ObjectInputStream) session.getAttribute("ois");
        if (ois == null) ois = new ObjectInputStream(s.getInputStream());
        session.setAttribute("ois", ois);

        ReponseTICKMAP rep = (ReponseTICKMAP) ois.readObject();
        if (rep.getCode() == TypeReponseTICKMAP.OK) {
            if (rep.getParam() != null) {
                challenge = (int) rep.getParam();
                oos.writeObject(new RequeteTICKMAP(TypeRequeteTICKMAP.Login, new Login(username, DigestCalculator.hashPassword(password, challenge))));
                rep = (ReponseTICKMAP) ois.readObject();
                System.out.print("Log: ");
                switch (rep.getCode()) {
                    case UNKNOWN_LOGIN:
                        request.setAttribute("Bad", "Login");
                        System.out.println("UNKNOWN_LOGIN");
                        return 1;
                    case BAD_PASSWORD:
                        request.setAttribute("Bad", "Password");
                        System.out.println("BAD_PASSWORD");
                        return 2;
                    case OK:
                        KeyExchange(request);
                        session.setAttribute("log", true);
                        System.out.println("OK");
                        return 0;
                }
            }
        }
        return -1;
    }

    private boolean NewUser(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        String username = request.getParameter("username");
        String mail     = request.getParameter("mail");
        String password = request.getParameter("password");
        String prenom   = request.getParameter("fname");
        String nom      = request.getParameter("lname");
        Socket s        = new Socket();

        s.connect(new InetSocketAddress(InetAddress.getByName(PropertiesReader.getProperties("BILLETS_NAME")), Integer.valueOf(PropertiesReader.getProperties("PORT_BILLETS"))), 5000);
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        oos.writeObject(new TickmapThreadRequest());

        oos = new ObjectOutputStream(s.getOutputStream());
        oos.writeObject(new RequeteTICKMAP(TypeRequeteTICKMAP.New_Users));

        Cipher           cipher = genPublicKey();
        WebUser          wu     = new WebUser(nom, prenom, mail, username, password);
        byte[]           enc    = FonctionsCrypto.encrypt(wu, cipher);
        DataOutputStream dos    = new DataOutputStream(s.getOutputStream());
        dos.write(enc);
        dos.flush();

        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
        ReponseTICKMAP    rep = (ReponseTICKMAP) ois.readObject();

        oos.writeObject(new RequeteTICKMAP(TypeRequeteTICKMAP.Disconnect));

        request.setAttribute("username", rep.getCode() == TypeReponseTICKMAP.OK
                                         ? username
                                         : true);
        return rep.getCode() == TypeReponseTICKMAP.OK;
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
    private void KeyExchange(HttpServletRequest request)
    throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException,
           CertificateException, KeyStoreException, NoSuchPaddingException, IOException,
           BadPaddingException, IllegalBlockSizeException, ClassNotFoundException,
           InvalidAlgorithmParameterException {
        ReponseTICKMAP    rep;
        HttpSession       session = request.getSession();
        Socket            s       = (Socket) session.getAttribute("s");
        ObjectInputStream ois     = (ObjectInputStream) session.getAttribute("ois");
        do {
            Cipher         cipher = genPublicKey();
            CryptedPackage cp     = new CryptedPackage(genSecretAES(), genAESParams());
            System.out.println("Création du flux");
            DataOutputStream      dos     = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
            ByteArrayOutputStream baos    = new ByteArrayOutputStream();
            ObjectOutputStream    tempoos = new ObjectOutputStream(baos);
            tempoos.writeObject(cp);
            dos.write(cipher.doFinal(baos.toByteArray()));
            dos.flush();

            rep = (ReponseTICKMAP) ois.readObject();
            session.setAttribute("cp", cp);

            AESCryptedSocket cryptedSocket = new AESCryptedSocket(s, cp.getParams());
            session.setAttribute("cryptedSocket", cryptedSocket);

            Mac hmac = Mac.getInstance("HMAC-SHA1", "BC");
            hmac.init(cp.getKey());
            session.setAttribute("hmac", hmac);
        } while (rep.getCode() != TypeReponseTICKMAP.OK);
        Table vols = (Table) rep.getParam();
        session.setAttribute("vols", vols);
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
    private Cipher genPublicKey()
    throws KeyStoreException, IOException, NoSuchPaddingException, NoSuchAlgorithmException,
           NoSuchProviderException, InvalidKeyException, CertificateException {
        System.out.println("Récupération de la clef publique du serveur");
        KeyStore        ks          = FonctionsCrypto.loadKeyStore(keystore, password.toCharArray());
        X509Certificate certificate = (X509Certificate) ks.getCertificate(keyname);
        PublicKey       pk          = certificate.getPublicKey();
        System.out.println("GETINSTANCE");
        Cipher cipher = Cipher.getInstance(pk.getAlgorithm(), "BC");
        System.out.println("GETINSTANCE");
        cipher.init(Cipher.PUBLIC_KEY, pk);
        return cipher;
    }

    /**
     * @return
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     */
    private SecretKey genSecretAES()
    throws NoSuchProviderException, NoSuchAlgorithmException {
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
    private AESParams genAESParams()
    throws NoSuchProviderException, NoSuchAlgorithmException {
        System.out.print("Génération de la clef de session ");
        SecretKey secretKey = genSecretAES();
        System.out.print(".");
        byte[] init = new byte[16];
        System.out.print(".");
        new SecureRandom().nextBytes(init);
        System.out.println(".");
        return new AESParams(secretKey, init);
    }

}
