package org.royrvik.capgeminiemr.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by Laxcor.
 */
public class RemoteServiceConnection implements ServiceConnection {

    private Context context;

    /**
     * Constructor
     * @param context The {@linkplain android.content.Context} used to bind to the service
     */
    public RemoteServiceConnection(Context context) {
        super();
        this.context = context;
    }

    EMR_RemoteService service;

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = EMR_RemoteService.Stub.asInterface(iBinder);
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
        i.setClassName("org.royrvik.capgeminiemr",org.royrvik.capgeminiemr.utils.EMR_RemoteService.class.getName());
        return context.bindService(i, this, Context.BIND_AUTO_CREATE);
    }

    /**
     * Unbinds from the service
     */
    public void releaseService() {
        context.unbindService(this);
    }
}
