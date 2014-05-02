package org.royrvik.capgeminiemr.preferences;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

class DownloadXmlTask extends AsyncTask<String, Void, String> {

    protected String doInBackground(String... url) {
        URL oracle;
        String output = "";

        try {

            oracle = new URL(url[0]);

            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null)
                output += inputLine;
            in.close();

        } catch (MalformedURLException e) {
            return "";
        } catch (IOException e) {
            return "";
        }

        return output;

    }

    protected void onPostExecute(Long result) {

    }
}
