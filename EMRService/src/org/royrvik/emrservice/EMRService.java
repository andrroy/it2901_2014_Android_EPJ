package org.royrvik.emrservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

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

    private final EMRRemoteInterface.Stub service = new EMRRemoteInterface.Stub() {
        @Override
        public List<String> getPatientData(String ssn, String username, String password) throws RemoteException {

            List<String> patientData = new ArrayList<String>();
            patientData.add(ssn);

            try {
                String name = new GetNameTask(ssn,username,password).execute().get();
                if(name == null) return null;
                patientData.add(name);
                return patientData;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public boolean upload(List<String> patientData, List<String> imagePaths, List<String> notes, String username, String password){

            try {
                return new UploadImagesTask(patientData, imagePaths, notes, username, password).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return false;
        }

    };
}