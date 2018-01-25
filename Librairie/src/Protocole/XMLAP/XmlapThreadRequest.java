package Protocole.XMLAP;

import NetworkObject.Bean.Voyageur;
import NetworkObject.Company.ICompany;
import ServeurClientLog.Objects.ServeurRequete;
import Tools.AESCryptedSocket;
import Tools.Bd.Bd;
import Tools.Procedural;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.crypto.Mac;
import javax.swing.text.Document;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XmlapThreadRequest extends ServeurRequete {


    @Override
    public Runnable createRunnable(Socket client) {
        return () -> {
            // Machines a etat
            boolean log = false;
            // Fin machine à état

            Bd                 bd            = null;
            boolean            boucle        = true;

            ObjectInputStream  ois;
            ObjectOutputStream oos;

            try {
                ois = new ObjectInputStream(client.getInputStream());
                oos = new ObjectOutputStream(client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            while (boucle) {
                try {
                    RequeteXMLAP req = (RequeteXMLAP) ois.readObject();
                    System.out.println("RequeteXMLAP recue : "+req.getType().toString());
                    ReponseXMLAP rep;
                    HeaderRunnable(req, Procedural.StringIp(client));//Pour log Affichage
                    switch (req.getType()) {
                        case AjoutVols:
                            //Copie du contenu de l'input stream dans un objet File
                            //BufferedReader bfr = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            File file = new File("vols.xml");
                            OutputStream outputStream = new FileOutputStream(file);
                            IOUtils.copy(client.getInputStream(), outputStream);
                            outputStream.close();
                            //FileUtils.copyInputStreamToFile(client.getInputStream(), file);
                            System.out.println("Taille du fichier : " + file.getTotalSpace());
                            //ParserCompanyDOM parser =
                            rep = new ReponseXMLAP(TypeReponseXMLAP.OK);
                            System.out.println(Thread.currentThread().getName() + "> Fichier xml traité.");
                            Reponse(oos, rep);
                        break;
                        case Connect: break;

                        case Disconnect:
                            boucle = false;
                            System.out.println(Thread.currentThread().getName() + "> Déconnexion de " + Procedural.StringIp(client));
                            break;
                    }
                } catch (Exception e) {
                    Logger.getGlobal().log(Level.WARNING, e.getClass().getName(), e);
                    if (e.getClass() != EOFException.class)
                        System.out.println(e.getMessage());
                    else
                        return;
                }
            }
        };
    }

}