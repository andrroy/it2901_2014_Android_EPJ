package org.royrvik.capgeminiemr.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import com.example.EMRService.EMRRemoteInterface;

import java.util.List;

/**
 * Created by Joakim.
 */
public class RemoteServiceConnection implements ServiceConnection {

    private Context context;
    private EMRRemoteInterface service;

    /**
     * Constructor
     * @param context The {@linkplain android.content.Context} used to bind to the service
     */
    public RemoteServiceConnection(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = EMRRemoteInterface.Stub.asInterface(iBinder);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    /**
     * Binds the context to the service
     * @return True if the connection was successful
     */
    public boolean bindService() {
        Intent i = new Intent();
        i.setClassName("com.example.EMRService","com.example.EMRService.EMRService");
        return context.bindService(i, RemoteServiceConnection.this, Context.BIND_AUTO_CREATE);
    }

    /**
     * Get patient data from the service
     * @param ssn The ssn for the person
     * @return An {@linkplain java.util.ArrayList}<{@linkplain java.lang.String}> with patient data.
     */
    public List<String> getPatientData(String ssn) {
        try {
            List<String> data = service.getPatientData(ssn);
            return data;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Upload data to the service
     * @param patientData An {@linkplain java.util.ArrayList}<{@linkplain java.lang.String}> with patient data.
     * @param imagePaths An {@linkplain java.util.ArrayList}<{@linkplain java.lang.String}> with image paths.
     * @return
     */
    public boolean upload(List<String> patientData, List<String> imagePaths) {
        try {
            return service.upload(patientData, imagePaths);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Unbinds from the service
     */
    public void releaseService() {
        context.unbindService(this);
    }
}
