package org.royrvik.capgeminiemr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.royrvik.capgeminiemr.qrscan.IntentIntegrator;
import org.royrvik.capgeminiemr.qrscan.IntentResult;
import org.royrvik.capgeminiemr.utils.RemoteServiceConnection;
import org.royrvik.capgeminiemr.utils.SessionManager;

import java.util.ArrayList;


public class IdentifyPatientActivity extends ActionBarActivity {

    private Button backButton, okButton;
    private ImageButton manualButton, automaticButton;
    private EditText patientIDEditText;
    private TextView error, offlineMessage;
    private ViewFlipper flipper;
    private ArrayList<String> incomingImages;
    private boolean returnAfter = false;
    private SessionManager session;
    private RemoteServiceConnection service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identify);

        //Getting the session
        session = new SessionManager(getApplicationContext());


        //Starting connection service
        service = new RemoteServiceConnection(getApplicationContext());
        if (!service.bindService()) {
            Toast.makeText(getApplicationContext(), "Could not connect to the EMR service", Toast.LENGTH_SHORT).show();
            finish();
        }

        // get intent from last activity
        Intent i = getIntent();
        incomingImages = i.getStringArrayListExtra("chosen_images");
        returnAfter = i.getBooleanExtra("return", false);

        flipper = (ViewFlipper) findViewById(R.id.identifyFlipper);
        patientIDEditText = (EditText) findViewById(R.id.editText);
        error = (TextView) findViewById(R.id.errorText);
        offlineMessage = (TextView) findViewById(R.id.offlineMessage);

        if (session.isValid()) {
            offlineMessage.setText("");
        } else {
            offlineMessage.setText("Currently in offline mode");
        }

        manualButton = (ImageButton) findViewById(R.id.manualButton);
        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipper.showNext();
            }
        });

        automaticButton = (ImageButton) findViewById(R.id.automaticButton);
        automaticButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(IdentifyPatientActivity.this);
                integrator.initiateScan();
            }
        });

        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientIDEditText.setText("");
                error.setText("");
                flipper.showPrevious();
            }
        });

        okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!patientIDEditText.getText().toString().trim().isEmpty()) {
                    checkPid(patientIDEditText.getText().toString());
                } else
                    Toast.makeText(getApplicationContext(), "Please enter a social security number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Checks patients ID.
     * Starts ExaminationActivity or returns to the launcher if PID is accepted
     */
    private void checkPid(String ssn) {
        //TODO: Show that the app is working on something
        ArrayList<String> info = new ArrayList<String>();
        if (session.isValid()) {
            // TODO: Remove hard coded password
            //TEMP data:
            String username = "rikardbe_emr";
            String password = "Paa5Eric";

            info = (ArrayList<String>) service.getPatientData(ssn, username, password);
        } else {
            info.add(patientIDEditText.getText().toString());
        }
        if (info != null) {
            if (returnAfter) {
                Intent data = new Intent();
                data.putStringArrayListExtra("patient", info);
                setResult(RESULT_OK, data);
                returnAfter = false;
            } else {
                Intent i = new Intent(IdentifyPatientActivity.this, ExaminationActivity.class);
                i.putStringArrayListExtra("info", info);
                i.putStringArrayListExtra("chosen_images", incomingImages);
                startActivity(i);
            }
            finish();
        } else Toast.makeText(getApplicationContext(), "Invalid ID", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back button clicked.
                if (flipper.getDisplayedChild() > 0) {
                    flipper.showPrevious();
                    break;
                }
                // Exit activity and open previous in activity stack
                finish();
                break;
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // QR code result format: xxxxxxxxxxx,Magnus Lund
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanResult != null) {
            if (scanResult.getFormatName().equals("QR_CODE")) {
                String ssn = scanResult.getContents();
                checkPid(ssn);
            } else if (scanResult.getFormatName().equals("CODE_128")) {
                String ssn = scanResult.getContents();
                checkPid(ssn);
            } else {
                Log.d("APP", "Invalid format"); //Give user feedback
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        service.releaseService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSession();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSession();
    }

    private void updateSession() {
        if (session.isValid()) {
            session.updateSession();
        }
    }
}
