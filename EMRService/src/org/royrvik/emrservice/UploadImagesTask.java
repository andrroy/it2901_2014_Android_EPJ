package org.royrvik.emrservice;

import android.os.AsyncTask;
import android.util.Log;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class UploadImagesTask extends AsyncTask<String, Void, Boolean> {

    private List<String> patientData;
    private List<String> imagePaths;
    private List<String> notes;

    private String username;
    private String password;

    public UploadImagesTask(List<String> patientData, List<String> imagePaths, List<String> notes, String username, String password){
        this.patientData = patientData;
        this.imagePaths = imagePaths;
        this.notes = notes;

        this.username = username;
        this.password = password;
    }

    protected Boolean doInBackground(String... userInformation){

        String imageServer = "royrvik.org";
        int serverPort = 22;
        String workingDir = "/home/rikardbe_emr/";

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        try{
            //Setting up connection to sftp server
            JSch jsch = new JSch();
            session = jsch.getSession(username,imageServer,serverPort);
            session.setPassword(password);
            java.util.Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp)channel;

            //Creating name of the folder, which is the current unix time stamp
            long unixTime = System.currentTimeMillis() / 1000L;
            String directoryName = Long.toString(unixTime);

            //Create and enter directory
            channelSftp.cd(workingDir);
            channelSftp.mkdir(directoryName);
            channelSftp.cd(directoryName);

            //Uploading image files while using the iterator to getting notes for each image as well
            int imageNumber=1; //Images are renamed to image1, image2 etc
            String body="Notes: \n"; //This string contains all notes

            for(String path : imagePaths){
                File f = new File(path);
                String imageName = "image"+(imageNumber)+f.getName().substring(f.getName().lastIndexOf("."));
                FileInputStream fs = new FileInputStream(f);
                channelSftp.put(new BufferedInputStream(fs), imageName);
                body+= "Image" + imageNumber + ": \n";
                body += notes.get(imageNumber-1) + "\n";
                imageNumber++;
            }

            //Creating head of document, containing patient information
            String head = "";
            head += "==================================\n";
            for(String data : patientData){
                head += data + "\n";
            }
            head += "==================================\n";

            //Assembling head and body document, making it ready to be written to file
            String info = head + body;
            //Uploading text file to server
            InputStream stream = new ByteArrayInputStream(info.getBytes("UTF-8"));
            channelSftp.put(stream, "info.txt");
            stream.close();

            //Closing connection
            channelSftp.exit();
            session.disconnect();

            //Returning true, since no exception was cast and upload was successful
            return true;

        }catch (Exception e){
            Log.d("APP", "Unable to upload image error: " + e.toString());
        }

        return false;
    }
}
