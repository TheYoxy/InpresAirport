package Servlets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import NetworkObject.Bean.Carte;
import NetworkObject.Bean.MACMessage;
import NetworkObject.Bean.Places;
import NetworkObject.Bean.Voyageur;
import Protocole.PAYP.ReponsePAYP;
import Protocole.PAYP.RequetePAYP;
import Protocole.PAYP.TypeReponsePAYP;
import Protocole.PAYP.TypeRequetePAYP;
import Protocole.TICKMAP.ReponseTICKMAP;
import Protocole.TICKMAP.RequeteTICKMAP;
import Protocole.TICKMAP.TypeReponseTICKMAP;
import Temp.PropertiesReader;
import Tools.AESCryptedSocket;
import Tools.Crypto.FonctionsCrypto;
import Tools.FonctionsPayement;

@WebServlet(name = "MainServlet", value = "/Main")
public class MainServlet extends HttpServlet {
    private static final String KEYSTORE     = "Application_Web.pkcs12";
    private static final String KEY_PAYEMENT = "serveurpayement";
    private static final String STOREPASS    = "azerty";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            WebType wt = WebType.valueOf(request.getParameter("Type"));
            switch (wt) {
                case PLACES:
                    request.setAttribute("nbPlaces", request.getParameter("Places"));
                    request.getRequestDispatcher("DetailsPassagers.jsp").forward(request, response);
                    break;
                case PASSAGERS: {
                    List<Voyageur>        list   = new LinkedList<>();
                    Map<String, String[]> map    = request.getParameterMap();
                    int                   lenght = map.get("Nom").length;
                    DateTimeFormatter     dtf    = DateTimeFormatter.ofPattern("d-MM-yyyy");
                    for (int i = 0; i < lenght; i++)
                        list.add(new Voyageur(map.get("Nom")[i], map.get("Prenom")[i], LocalDate.parse(map.get("Naissance")[i], dtf)));
                    HttpSession session = request.getSession();
                    session.setAttribute("list", list);
                    Places p = FonctionsPayement.sendVoyageur((ObjectOutputStream) session.getAttribute("oos"),
                            (AESCryptedSocket) session.getAttribute("cryptedSocket"),
                            (ObjectInputStream) session.getAttribute("ois"),
                            ((Vector<String>) session.getAttribute("selected")).get(0),
                            list);
                    session.setAttribute("p", p);
                    request.getRequestDispatcher("ConfirmPlaces.jsp").forward(request, response);
                    break;
                }
                case CONFIRMATION_PLACES:
                    if (Boolean.valueOf(request.getParameter("Conf"))) {
                        HttpSession session = request.getSession();
                        FonctionsPayement.confirmTICKMAP((Mac) session.getAttribute("hmac"),
                                (ObjectOutputStream) session.getAttribute("oos"));
                        request.getRequestDispatcher("Carte.jsp").forward(request, response);
                    }
                    else {
                        System.out.println("INVALIDATE");
                        request.getSession().invalidate();
                        request.getRequestDispatcher("index.jsp").forward(request, response);
                    }
                    break;
                case CARTE: {
                    HttpSession    session = request.getSession();
                    List<Voyageur> list    = (List<Voyageur>) session.getAttribute("list");
                    Voyageur       v       = list.get(Integer.valueOf(request.getParameter("proprio")));
                    String         card    = request.getParameter("card");

                    Cipher asym     = FonctionsCrypto.loadPublicKey(KEYSTORE, STOREPASS, KEY_PAYEMENT);
                    Carte  c        = new Carte(v, card);
                    Socket payement = new Socket(InetAddress.getByName(PropertiesReader.getProperties("PAYEMENT_NAME")), Integer.parseInt(PropertiesReader.getProperties("PORT_PAYEMENT")));
                    request.setAttribute("c", c);
                    final double solde = (request.getParameter("new") != null
                            && !request.getParameter("new").equals("")
                            && Boolean.parseBoolean(request.getParameter("new")))
                                         ? Double.parseDouble(request.getParameter("solde"))
                                         : 0.00;

                    RequeteTICKMAP requeteTICKMAP = FonctionsPayement.sendPayement(c, asym, payement, ((Places) session.getAttribute("p")).getPrix(), (Mac) session.getAttribute("hmac"), (oosPayement, oisPayement) -> {
                        if (solde == 0.00) return false;
                        RequetePAYP requetePAYP = new RequetePAYP(TypeRequetePAYP.NEW_CARD, FonctionsCrypto.encrypt(c, asym), FonctionsCrypto.encrypt(solde, asym));
                        oosPayement.writeObject(requetePAYP);
                        ReponsePAYP reponse = (ReponsePAYP) oisPayement.readObject();
                        return reponse.getCode() == TypeReponsePAYP.OK;
                    });
                    ObjectOutputStream oos = (ObjectOutputStream) session.getAttribute("oos");
                    oos.writeObject(requeteTICKMAP);

                    ObjectInputStream ois = (ObjectInputStream) session.getAttribute("ois");
                    ReponseTICKMAP    rep = (ReponseTICKMAP) ois.readObject();
                    //TODO TEST VALEUR DE REP

                    request.setAttribute("id", rep.getCode() == TypeReponseTICKMAP.OK
                                               ? (String) ((MACMessage) requeteTICKMAP.getParam()).getParam()
                                               : null);
                    request.getRequestDispatcher(rep.getCode() == TypeReponseTICKMAP.OK
                                                 ? "valide.jsp"
                                                 : "fail.jsp").forward(request, response);
                }
                break;
            }
        } catch (NullPointerException e) {
            Vector<String> selected = new Vector<>();
            for (Map.Entry<String, String[]> entry :
                    request.getParameterMap().entrySet()) {
                if (org.apache.commons.lang3.StringUtils.isNumeric(entry.getKey().trim())) {
                    String[] val = entry.getValue();
                    val[0] = val[0].trim();
                    selected.add(val[0]);
                }
            }
            System.out.println("Selected= " + selected);
            request.getSession().setAttribute("selected", selected);
            request.getRequestDispatcher("Places.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("From", "Login.doPost 3");
            request.setAttribute("Exception", e);
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    public enum WebType {
        PLACES, PASSAGERS, CONFIRMATION_PLACES, CARTE
    }
}
