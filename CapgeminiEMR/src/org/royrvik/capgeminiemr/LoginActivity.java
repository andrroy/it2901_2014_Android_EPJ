package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.ArrayList;


public class LoginActivity extends Activity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private ArrayList<String> incomingImages;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);

        // get intent from launcher
        Intent i = getIntent();
        incomingImages = i.getStringArrayListExtra("chosen_images");
        Crouton.makeText(LoginActivity.this, "Recieved " + Integer.toString(incomingImages.size())  + " images from launcher", Style.INFO).show();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doLogin()) {
                    Intent i = new Intent(LoginActivity.this, IdentifyPatientActivity.class);
                    i.putStringArrayListExtra("chosen_images", incomingImages);
                    startActivity(i);
                }
            }
        });


    }


    /**
     * Temporary login function
     *
     * @return
     */
    private boolean doLogin() {
        if (usernameEditText.getText().equals("a") && passwordEditText.getText().equals("a")) {
            return true;
        } else {
            Crouton.makeText(this, "Wrong username or password", Style.ALERT).show();
            return false;
        }
    }

    /**
     * Checks if application is started with/without images, if user is alraedy logged in etc..
     */

    private void startApplicationInMode() {

    }


}