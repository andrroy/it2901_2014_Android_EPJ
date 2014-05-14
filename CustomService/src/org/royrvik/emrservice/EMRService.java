package org.royrvik.emrservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EMRService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public IBinder onBind(Intent intent) {
        return service;
    }

//    private List<String> getPatientReturnMessage;
//    private List<String> uploadImagesReturnMessage;
//
//    public void setGetPatientReturnMessage(List<String> getPatientReturnMessage){
//        this.getPatientReturnMessage = getPatientReturnMessage;
//    }
//
//    public void setUploadImagesReturnMessage(List<String> uploadImagesReturnMessage){
//        this.uploadImagesReturnMessage = uploadImagesReturnMessage;
//    }




    private final EMRRemoteInterface.Stub service = new EMRRemoteInterface.Stub() {

        @Override
        public List<String> getPatientData(String ssn, String username, String password) throws RemoteException {
            ArrayList<String> returnMessage = null;
            try {
                returnMessage = new GetNameTask(ssn,username,password).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return returnMessage;

        }

        @Override
        public List<String> upload(List<String> examinationData, List<String> imagePaths, List<String> notes, String username, String password){

            ByteArrayOutputStream pdf = PDFCreator.createPDF(examinationData,imagePaths, notes, username);

            ArrayList<String> returnMessage = null;

            try {
                returnMessage =  new UploadImagesTask(pdf, imagePaths, username, password).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return returnMessage;
        }

    };
}