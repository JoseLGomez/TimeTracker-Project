package utils;

import android.content.res.XmlResourceParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Jesus on 07/01/2016.
 */
public class tools {

    /**
     *  Dado una path de un fihcero de configuracion y un elmento a buscar en formato XML lo busca.
     * @param xrp Path donde se encutrna el fichero
     * @param element etiqueta del elemento xml que deseas buscar
     * @return String elemento encontrado si no encuentra retorna un blanco
     */
    public static String readFileXML(String xrp,String element ) {
        String result = "";
        try {
            File fXmlFile = new File(xrp);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            //Asignamos que el primer nivel se llama config
            Node root = doc.getElementsByTagName("config").item(0);
            NodeList nList = root.getChildNodes();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node node = nList.item(temp);
                if (element.equals(node.getNodeName())) {
                     result = node.getTextContent();

                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Dado una path de fichero un tag y un valor remplaza el valor en un fichero xml
     * @param io Fichero de entrada
     * @param element tag que se quiere modificar
     * @param value valor al cual se quiere cmabiar
     */
    public static void writeFileXML(String io, String element, String value){
        try {
            File fXmlFile = new File(io);
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(fXmlFile);
            Node company = doc.getFirstChild();
            Node root = doc.getElementsByTagName("config").item(0);
            NodeList nList = root.getChildNodes();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node node = nList.item(temp);
                if (element.equals(node.getNodeName())) {
                    node.setTextContent(value);

                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(io));
            transformer.transform(source, result);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }
}
