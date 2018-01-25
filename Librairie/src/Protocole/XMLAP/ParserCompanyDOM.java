package Protocole.XMLAP;

import NetworkObject.Company.AbstractCompany;
import NetworkObject.Company.HighClass;
import NetworkObject.Company.LowCost;
import Tools.PropertiesReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ParserCompanyDOM {
    private final Document doc;

    public ParserCompanyDOM(File xmlFile)
    throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        //        domFactory.setValidating(true);
        //        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        doc = builder.parse(xmlFile);
    }

    public static void println(String s, int retrait) {
        retrait *= 2;
        for (int i = 0; i < retrait; i++) {
            System.out.print(" ");
        }
        System.out.println(s);
    }

    public void Affichage() {
        final Element el = doc.getDocumentElement();
        System.out.println(el.getNodeName());
        ParcourArbre(el, 0, doc);
    }

    public void ParcourArbre(Node noeud, int profondeur, Document doc) {
        System.out.println("Noeud: " + noeud);
        if (noeud == null) return;

        String nom    = noeud.getNodeName();
        String valeur = noeud.getNodeValue();
        AbstractCompany company = null;

        NodeList nl = noeud.getChildNodes();

        for(int i = 1; i<nl.getLength(); i+=2){
            //Chargement de la classe associée
            if(nl.item(i).getNodeName().equals("compagnie")) {
                if (PropertiesReader.getProperties(nl.item(i).getFirstChild().getNextSibling().getFirstChild().getNodeValue()).equals("LowCost")) {
                    company = new LowCost();
                } else {
                    company = new HighClass();
                }
            }
            if(nl.item(i).getNodeName().equals("createFlights")){
                //On recupere tous les vols
                NodeList nlVols = nl.item(i).getChildNodes();
                for(int j = 1; j<nlVols.getLength(); j+=2){
                    //On recupere les noeuds contenus dans chaque vols
                    NodeList nlVolItem = nlVols.item(j).getChildNodes();
                    company.createFlights(nlVolItem.item(1).getFirstChild().getNodeValue(), nlVolItem.item(3).getFirstChild().getNodeValue(), Timestamp.valueOf(nlVolItem.item(5).getFirstChild().getNodeValue()), Timestamp.valueOf(nlVolItem.item(7).getFirstChild().getNodeValue()), Float.parseFloat(nlVolItem.item(9).getFirstChild().getNodeValue()), nlVolItem.item(11).getFirstChild().getNodeValue(), Integer.parseInt(nlVolItem.item(13).getFirstChild().getNodeValue()), Integer.parseInt(nlVolItem.item(15).getFirstChild().getNodeValue()));
                    /*for(int k = 1; j<nlVolItem.getLength(); k+=2 ){
                        nlVolItem.item(k).getNodeName();
                    } */
                }
            }
        }

        NodeList enfants = noeud.getChildNodes();
        for (int i = 0; i < enfants.getLength(); i++) {
            //Appel récursif :
            //ParcourArbre(enfants.item(i), profondeur + 1);
        }
    }

    // Retourne la valeur du noeud transmis sous forme de string
    private String getString(Node n) {
        Node noeud = n.getChildNodes().item(0);
        if (noeud != null) {
            String text = noeud.getNodeValue().trim();
            return text;
        } else {
            return null;
        }
    }
}
