package Protocole.XMLAP;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class ParserCompanyDOM {

    public ParserCompanyDOM(File xmlFile){

        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setValidating(true);
            domFactory.setNamespaceAware(true);
            DocumentBuilder parserDOM = domFactory.newDocumentBuilder();
            Document doc = parserDOM.parse(xmlFile);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ParcourArbre(Node noeud, int profondeur){
        if(noeud == null) return;

        String nom = noeud.getNodeName();
        String valeur = noeud.getNodeValue();

        if(valeur != null) valeur = valeur.trim();

        if(noeud.getNodeType() == Node.TEXT_NODE || valeur.equals(""));
        else println(nom + " : " + valeur, profondeur);

        NodeList enfants = noeud.getChildNodes();
        for(int i = 0;i<enfants.getLength();i++){
            //Appel récursif :
            ParcourArbre(enfants.item(i), profondeur+1);
        }
    }

    public static void println(String s, int retrait){
        retrait *=2;
        for(int i=0; i<retrait; i++){
            System.out.print(" ");
        }
        System.out.println(s);
    }
}
