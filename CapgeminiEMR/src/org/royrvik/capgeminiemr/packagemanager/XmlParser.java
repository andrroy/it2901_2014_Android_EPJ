package org.royrvik.capgeminiemr.packagemanager;

import android.util.Log;
import org.royrvik.capgeminiemr.packagemanager.DownloadXmlTask;

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
