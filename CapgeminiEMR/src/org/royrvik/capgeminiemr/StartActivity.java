package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class StartActivity extends Activity {

    private static final String TAG = "APP";
    private ArrayList<String> incomingImages;

    private Button tempLoginButton, tempSettingsButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // gets intent from launcher
        Intent i = getIntent();
        incomingImages = i.getStringArrayListExtra("chosen_images");

        Log.w(TAG, incomingImages.toString());
        Toast.makeText(StartActivity.this, "Recieved " + Integer.toString(incomingImages.size()) + " images from launcher", Toast.LENGTH_LONG).show();

        tempLoginButton = (Button) findViewById(R.id.tempButton);
        tempLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, Login.class);
                startActivity(i);
            }
        });

        tempSettingsButton = (Button) findViewById(R.id.tempButton2);
        tempSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, HomeScreenActivity.class);
                startActivity(i);
            }
        });

    }
}
