package org.royrvik.capgeminiemr.utils;

public class XmlParser {
    public static String parse(String url){
        String xml;
        try{
        xml = new DownloadXmlTask().execute(url).get();
        } catch(Exception e){
            return null;
        }

        //XML must be parsed, and returned as an array

        return xml;
    }
}
