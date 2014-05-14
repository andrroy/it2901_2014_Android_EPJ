package org.royrvik.capgeminiemr.preferences;

import org.royrvik.capgeminiemr.preferences.DownloadXmlTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;

public class XmlParser {

    /**
     * Reads XML from URL and returns a Hashmap containing every node as nodekey:nodevalue
     * @param url URL to the XML
     * @return HashMap containing XML node name as key and node value as value
     */
    public static HashMap<String, String> parse(String url)throws Exception{
        String xml = "";
        // Hashmap containing the fetched values from XML as "nodeName:nodeValue"
        HashMap<String, String> xmlNodeHashMap = new HashMap<String, String>();

        // Download the XML file with helper class

        xml = new DownloadXmlTask().execute(url).get();


        // Parse the XML and save it in the HashMap
        Document xmlDocument = null;
        xmlDocument = loadXMLFromString(xml);


        NodeList entries = xmlDocument.getElementsByTagName("*");

        for (int i = 0; i < entries.getLength(); i++) {
            Element element = (Element) entries.item(i);
            if(!element.getNodeName().equals("settings")) {
                xmlNodeHashMap.put(element.getNodeName(), element.getTextContent());
            }
        }

        return xmlNodeHashMap;
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
