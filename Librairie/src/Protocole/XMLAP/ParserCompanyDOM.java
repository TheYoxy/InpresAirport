package Protocole.XMLAP;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

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
        ParcourArbre(el, 0);
    }

    public void ParcourArbre(Node noeud, int profondeur) {
        System.out.println("Noeud: " + noeud);
        if (noeud == null) return;

        String nom    = noeud.getNodeName();
        String valeur = noeud.getNodeValue();

        if (valeur != null) valeur = valeur.trim();

        if ((noeud.getNodeType() != Node.TEXT_NODE && (valeur == null || !valeur.equals(""))))
            println(nom + " : " + valeur, profondeur);

        NodeList enfants = noeud.getChildNodes();
        for (int i = 0; i < enfants.getLength(); i++) {
            //Appel récursif :
            ParcourArbre(enfants.item(i), profondeur + 1);
        }
    }
}
