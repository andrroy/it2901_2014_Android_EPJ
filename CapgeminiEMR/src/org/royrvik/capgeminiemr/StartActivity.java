package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class StartActivity extends Activity {

    private static final String TAG = "APP";
    private ArrayList<String> incomingImages;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // gets intent from launcher
        Intent i = getIntent();
        incomingImages = i.getStringArrayListExtra("chosen_images");

        Log.w(TAG, incomingImages.toString());
        Toast.makeText(StartActivity.this, "Recieved " + Integer.toString(incomingImages.size()) + " images from launcher", Toast.LENGTH_LONG).show();

    }
}
