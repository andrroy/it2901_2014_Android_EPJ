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

    private ByteArrayOutputStream pdf;
    private List<String> imagePaths;

    private String username;
    private String password;

    public UploadImagesTask(ByteArrayOutputStream pdf, List<String> imagePaths, String username, String password){

        this.pdf = pdf;
        this.imagePaths = imagePaths;

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

            //Uploading image files
            int imageNumber=1; //Images are renamed to image1, image2 etc

            for(String path : imagePaths){
                File f = new File(path);
                String imageName = "image"+(imageNumber)+f.getName().substring(f.getName().lastIndexOf("."));
                FileInputStream fs = new FileInputStream(f);
                channelSftp.put(new BufferedInputStream(fs), imageName);
                imageNumber++;
            }

            //Uploading pdf
            byte[] a = pdf.toByteArray();
            InputStream is = new ByteArrayInputStream(a);
            channelSftp.put(is, "examination.pdf");

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
