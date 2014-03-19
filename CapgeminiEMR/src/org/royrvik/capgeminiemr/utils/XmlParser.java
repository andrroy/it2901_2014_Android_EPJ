package org.royrvik.capgeminiemr.utils;

import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class XmlParser {
    public static void parse(String url) {
        String xml = "";
        try {
            xml = new DownloadXmlTask().execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Document xmlDocument = null;
        try {
            xmlDocument = loadXMLFromString(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }


        NodeList entries = xmlDocument.getElementsByTagName("*");

        for (int i = 0; i < entries.getLength(); i++) {
            Element element = (Element) entries.item(i);
            Log.d("APP", "Name: " + element.getNodeName() + ". Value: " + element.getTextContent());
        }

        //return xml;
    }

    /**
     * Helper method for converting XML as String to Document
     *
     * @param xml Xml as String
     * @return
     * @throws Exception
     */
    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
}
