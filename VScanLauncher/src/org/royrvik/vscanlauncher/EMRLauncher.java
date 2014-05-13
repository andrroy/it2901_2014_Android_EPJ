package org.royrvik.vscanlauncher;

import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;

/**
 * Created by Joakim.
 */

public class EMRLauncher extends Intent {

    private static final int START_IMAGES = 1;
    private static final int START_HOMESCREEN = 2;
    private static final int START_IMAGES_ID = 3;
    private static final int START_IDENTIFY = 4;

    private Context context;

    /**
     * Constructor used to launch the Gateway application to the Homescreen
     * @param context Application {@linkplain android.content.Context} from the launcher
     */
    public EMRLauncher(Context context) {
        super(context.getPackageManager().getLaunchIntentForPackage("org.royrvik.capgeminiemr"));
        putExtra("type", START_HOMESCREEN);
        this.context = context;
    }

    /**
     * Constructor used to launch the Gateway application to Identify a patient and return the gathered data.
     * @param context Application {@linkplain android.content.Context} from the launcher
     * @param broadcastCode A {@linkplain java.lang.String} with the broadcast code registered to the BroadcastReceiver in the launcher.
     */
    public EMRLauncher(Context context, String broadcastCode) {
        super(context.getPackageManager().getLaunchIntentForPackage("org.royrvik.capgeminiemr"));
        putExtra("type", START_IDENTIFY);
        putExtra("code", broadcastCode);
        this.context = context;
    }

    /**
     * Constructor used to launch the Gateway application to start with images.
     * @param context Application {@linkplain android.content.Context} from the launcher
     * @param images The {@linkplain java.util.ArrayList}<{@linkplain java.lang.String}> of images to send to the Gateway application
     */
    public EMRLauncher(Context context, ArrayList<String> images) {
        super(context.getPackageManager().getLaunchIntentForPackage("org.royrvik.capgeminiemr"));
        putExtra("type", START_IMAGES);
        putStringArrayListExtra("chosen_images", images);
        this.context = context;
    }

    /**
     * Constructor used to launch the Gateway application to start with images and patient data.
     * @param context Application {@linkplain android.content.Context} from the launcher
     * @param images The {@linkplain java.util.ArrayList}<{@linkplain java.lang.String}> of images to send to the Gateway application
     * @param patientData The {@linkplain java.util.ArrayList}<{@linkplain java.lang.String}> of patient data to send to the Gateway application
     */
    public EMRLauncher(Context context, ArrayList<String> images, ArrayList<String> patientData) {
        super(context.getPackageManager().getLaunchIntentForPackage("org.royrvik.capgeminiemr"));
        putExtra("type", START_IMAGES_ID);
        putStringArrayListExtra("chosen_images", images);
        putStringArrayListExtra("patientData", patientData);
        this.context = context;
    }

    /**
     * Starts the Gateway application with the constructed intent
     */
    public void start() {
        context.startActivity(this);
    }
}
