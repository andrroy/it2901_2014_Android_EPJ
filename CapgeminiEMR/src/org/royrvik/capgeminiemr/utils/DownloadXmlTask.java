package org.royrvik.capgeminiemr.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

class DownloadXmlTask extends AsyncTask<String, Void, String> {

    protected String doInBackground(String... url){
        URL oracle;
        String output = "";

        try{

            oracle = new URL(url[0]);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(oracle.openStream()));

                String inputLine;

                while ((inputLine = in.readLine()) != null)
                    output += inputLine;
                in.close();

        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        Log.d("APP", output);
        return output;

    }

    protected void onPostExecute(Long result) {

    }
}
