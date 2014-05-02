package org.royrvik.capgeminiemr.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import org.royrvik.capgeminiemr.EMRApplication;
import org.royrvik.emrservice.EMRRemoteInterface;

import java.util.List;

public class RemoteServiceConnection implements ServiceConnection {

    private Context context;
    private EMRRemoteInterface service;
    private EMRApplication settings;

    /**
     * Constructor
     *
     * @param context The {@linkplain android.content.Context} used to bind to the service
     */
    public RemoteServiceConnection(Context context) {
        super();
        this.context = context;
        settings = (EMRApplication) context.getApplicationContext();
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
     *
     * @return True if the connection was successful
     */
    public boolean bindService() {
        Intent i = new Intent();
        i.setClassName(settings.getSettingsAIDLLocation(), settings.getSettingsServicePath());
        return context.bindService(i, RemoteServiceConnection.this, Context.BIND_AUTO_CREATE);
    }

    /**
     * Get patient data from the service
     *
     * @param ssn The ssn for the person
     * @return An {@linkplain java.util.ArrayList}<{@linkplain java.lang.String}> with patient data.
     */
    public List<String> getPatientData(String ssn, String username, String password) {
        try {
            return service.getPatientData(ssn, username, password);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Upload data to the service
     *
     * @param patientData An {@linkplain java.util.ArrayList}<{@linkplain java.lang.String}> with patient data.
     * @param imagePaths  An {@linkplain java.util.ArrayList}<{@linkplain java.lang.String}> with image paths.
     * @return true if the data was uploaded successfully.
     */
    public boolean upload(List<String> patientData, List<String> imagePaths, List<String> notes, String username, String password) {
        try {
            return service.upload(patientData, imagePaths, notes, username, password);
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
