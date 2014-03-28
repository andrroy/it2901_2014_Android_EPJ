package org.royrvik.capgeminiemr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.utils.Encryption;
import org.royrvik.capgeminiemr.utils.SessionManager;

import java.util.ArrayList;


public class LoginActivity extends SherlockActivity {

    private static int RESULT_IDENTIFY_PATIENT = 2;

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private ArrayList<String> incomingImages;
    private String patientId;
    private int launcherCommand;
    private String broadcastCode = "";
    private SessionManager session;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        session = new SessionManager(getApplicationContext());

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);

        // get intent from launcher
        getInformationFromIntent(getIntent());

        //If the current session is valid
        if (session.isValid()) {
            startApplication();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creates a new login session with the credentials entered, encrypting the password
                session.createLoginSession(
                        usernameEditText.getText().toString(),
                        Encryption.encrypt(usernameEditText.getText().toString(), passwordEditText.getText().toString())
                );

                //Check if username/password is correct, and forwarding to next view if true
                if (session.isValid()) {
                    startApplication();
                }
                else{
                    Crouton.makeText(LoginActivity.this, "Wrong username and/or password", Style.ALERT).show();
                }
                passwordEditText.setText("");
            }
        });

    }

    private void getInformationFromIntent(Intent i) {
        launcherCommand = i.getIntExtra("type", 0);
        switch (launcherCommand) {
            case 1: //Images
                incomingImages = i.getStringArrayListExtra("chosen_images");
                Crouton.makeText(LoginActivity.this, "Received " + Integer.toString(incomingImages.size()) + " images from launcher", Style.INFO).show();
                break;
            case 2: //No images
                Crouton.makeText(LoginActivity.this, "Received no images from launcher", Style.INFO);
                break;
            case 3: //Images and ID
                incomingImages = i.getStringArrayListExtra("chosen_images");
                Crouton.makeText(LoginActivity.this, "Received " + Integer.toString(incomingImages.size()) + " images and ID from launcher", Style.INFO).show();
                patientId = i.getStringExtra("id");
                break;
            case 4: //Identify
                Crouton.makeText(LoginActivity.this, "Identify Patient", Style.INFO);
                break;
            default:
                finish();
        }
        broadcastCode = i.getStringExtra("code");
    }

    private void startApplication() {
        Intent i;
        switch (launcherCommand) {
            case 1: //Images
                i = new Intent(LoginActivity.this, IdentifyPatientActivity.class);
                i.putStringArrayListExtra("chosen_images", incomingImages);
                startActivity(i);
                finish();
                break;
            case 2: //No images
                i = new Intent(LoginActivity.this, HomeScreenActivity.class);
                startActivity(i);
                finish();
                break;
            case 3: //Images and ID
                i = new Intent(LoginActivity.this, IdentifyPatientActivity.class);
                i.putStringArrayListExtra("chosen_images", incomingImages);
                i.putExtra("id", patientId);
                startActivity(i);
                finish();
                break;
            case 4: //Identify
                i = new Intent(LoginActivity.this, IdentifyPatientActivity.class);
                i.putExtra("return", true);
                startActivityForResult(i, RESULT_IDENTIFY_PATIENT);
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_IDENTIFY_PATIENT) {
            if (resultCode == RESULT_OK && data != null) {
                Intent i = new Intent(broadcastCode);
                i.putStringArrayListExtra("patient", data.getStringArrayListExtra("patient"));
                sendBroadcast(i);
            }
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.xml.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings_button:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}