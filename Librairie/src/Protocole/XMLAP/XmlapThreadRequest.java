package Protocole.XMLAP;

import org.apache.commons.io.IOUtils;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import ServeurClientLog.Objects.ServeurRequete;
import Tools.Bd.Bd;
import Tools.Procedural;

public class XmlapThreadRequest extends ServeurRequete {


    @Override
    public Runnable createRunnable(Socket client) {
        return () -> {
            // Machines a etat
            boolean log = false;
            // Fin machine à état

            Bd      bd     = null;
            boolean boucle = true;

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
                    ReponseXMLAP rep;
                    HeaderRunnable(req, Procedural.StringIp(client));//Pour log Affichage
                    switch (req.getType()) {
                        case AjoutVols:
                            File file = new File("xml" + System.getProperty("file.separator") + "temp.xml");
                            file.deleteOnExit();
                            System.out.println(Thread.currentThread().getName() + "> Ouverture du fichier xml" + System.getProperty("file.separator") + "temp.xml");

                            FileOutputStream fos = new FileOutputStream(file);
                            int size = IOUtils.copy(client.getInputStream(), fos);
                            System.out.println(Thread.currentThread().getName() + "> Lu: " + size);
                            fos.flush();
                            fos.close();
                            System.out.println(Thread.currentThread().getName() + "> Filesize: " + file.length());

                            ParserCompanyDOM parser = new ParserCompanyDOM(file);
                            parser.Affichage();
                            rep = new ReponseXMLAP(TypeReponseXMLAP.OK);
                            System.out.println(Thread.currentThread().getName() + "> Fichier xml traité.");
                            Reponse(oos, rep);
                            break;
                        case Connect:
                            break;

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