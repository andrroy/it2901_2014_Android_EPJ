package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.ArrayList;

public class StartActivity extends Activity {

    private static final String TAG = "APP";
    private ArrayList<String> incomingImages;

    private Button tempLoginButton, tempSettingsButton, tempHomeButton, tempTechsetupButton, tempIdentifyButton, tempExaminationButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // get intent from launcher
        Intent i = getIntent();
        incomingImages = i.getStringArrayListExtra("chosen_images");

        Crouton.makeText(StartActivity.this, "Recieved " + Integer.toString(incomingImages.size())  + " images from launcher", Style.INFO).show();

        tempLoginButton = (Button) findViewById(R.id.tempButton);
        tempLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        tempSettingsButton = (Button) findViewById(R.id.tempButton2);
        tempSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        tempHomeButton = (Button) findViewById(R.id.tempButton3);
        tempHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, HomeScreenActivity.class);
                i.putStringArrayListExtra("chosen_images", incomingImages);
                startActivity(i);
            }
        });

        tempTechsetupButton = (Button) findViewById(R.id.tempButton4);
        tempTechsetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, TechnicalSetupActivity.class);
                startActivity(i);
            }
        });

        tempIdentifyButton = (Button) findViewById(R.id.tempButton5);
        tempIdentifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StartActivity.this, IdentifyPatientActivity.class);
                i.putStringArrayListExtra("chosen_images", incomingImages);
                startActivity(i);
            }
        });
    }
}
