package Tools;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;

import NetworkObject.Bean.Carte;
import NetworkObject.Bean.MACMessage;
import NetworkObject.Bean.Payement;
import NetworkObject.Bean.Places;
import NetworkObject.Bean.Voyageur;
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
import Tools.Crypto.FonctionsCrypto;
import Tools.Exceptions.PayementException;

public abstract class FonctionsPayement {

    public static void confirmTICKMAP(Mac hmac, ObjectOutputStream oos)
    throws IOException {
        oos.writeObject(new ReponseTICKMAP(TypeReponseTICKMAP.OK, new MACMessage(hmac, new ReponseTICKMAP(TypeReponseTICKMAP.OK))));
    }

    public static RequeteTICKMAP sendPayement(Carte c, Cipher asym, Socket payement, double prix, Mac hmac, newCard newCardHandler)
    throws IOException, ClassNotFoundException, BadPaddingException, IllegalBlockSizeException {
        boolean            pay            = true;
        RequeteTICKMAP     requeteTICKMAP = null;
        ObjectOutputStream oosPayement    = new ObjectOutputStream(payement.getOutputStream());
        oosPayement.writeObject(new PAYPThreadRequest());
        System.out.println("THREADREQUEST");
        oosPayement = new ObjectOutputStream(payement.getOutputStream());
        ObjectInputStream oisPayement = new ObjectInputStream(payement.getInputStream());
        //TODO AJOUT SIGNATURE
        while (pay) {
            System.out.println("PAYEMENT");
            oosPayement.writeObject(new RequetePAYP(TypeRequetePAYP.PAYEMENT, (Serializable) FonctionsCrypto.encrypt(new Payement(c, prix), asym)));
            ReponsePAYP reponse = (ReponsePAYP) oisPayement.readObject();
            System.out.println("Reponse: " + reponse);
            System.out.println("Param:   " + reponse.getParam());
            System.out.println("Param: (classname) " + reponse.getParam().getClass().getName());

            if (reponse.getCode() == TypeReponsePAYP.OK) {
                requeteTICKMAP = new RequeteTICKMAP(TypeRequeteTICKMAP.Confirm_Payement, new MACMessage(hmac, reponse.getParam()));
                pay = false;
            }
            else if (reponse.getParam() == TypeReponseST.CARD_NOT_FOUND) {
                pay = newCardHandler.sendNewCard(oosPayement, oisPayement);
                if (!pay)
                    requeteTICKMAP = new RequeteTICKMAP(TypeRequeteTICKMAP.Payement_Abort, "Card not foud");
            }
            else {
                // TODO AJOUT DU MAC
                requeteTICKMAP = new RequeteTICKMAP(TypeRequeteTICKMAP.Payement_Abort, reponse.getParam());
                pay = false;
            }
        }
        return requeteTICKMAP;
    }

    public static Places sendVoyageur(ObjectOutputStream oos, AESCryptedSocket cryptedSocket, ObjectInputStream ois, String vol, List<Voyageur> list)
    throws IOException, BadPaddingException, IllegalBlockSizeException, ClassNotFoundException,
           PayementException {
        oos.writeObject(new RequeteTICKMAP(TypeRequeteTICKMAP.Ajout_Voyageurs));
        List l = new LinkedList<>();
        l.add(vol);
        l.add(list.toArray(new Voyageur[list.size()]));
        cryptedSocket.writeObject((Serializable) l);
        ReponseTICKMAP rep = (ReponseTICKMAP) ois.readObject();
        if (rep.getCode() == TypeReponseTICKMAP.FULL)
            throw new PayementException(PayementExceptionType.FULL, rep);
        else if (rep.getCode() != TypeReponseTICKMAP.OK)
            throw new PayementException(PayementExceptionType.OTHER, rep);
        else
            return (Places) cryptedSocket.readObject();
    }

    public interface newCard {
        boolean sendNewCard(ObjectOutputStream oos, ObjectInputStream ois)
        throws IOException, ClassNotFoundException, BadPaddingException, IllegalBlockSizeException;
    }
}
