package org.royrvik.capgeminiemr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import org.royrvik.capgeminiemr.qrscan.IntentIntegrator;
import org.royrvik.capgeminiemr.qrscan.IntentResult;
import org.royrvik.capgeminiemr.utils.RemoteServiceConnection;
import org.royrvik.capgeminiemr.utils.SessionManager;

import java.util.ArrayList;


public class IdentifyPatientActivity extends SherlockActivity {

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
        //Actionbarsherlock back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Starting connection service
        service = new RemoteServiceConnection(getApplicationContext());
        if (!service.bindService()) {
            Toast.makeText(getApplicationContext(), "Could not connect to the EMR service", Toast.LENGTH_SHORT);
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
        }
        else {
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
                if (!patientIDEditText.getText().toString().trim().isEmpty()) checkPid();
                else error.setText("Invalid ID format.");
            }
        });

        //If activity was started with id
        String id = i.getStringExtra("id");
        if (id != null) {
            patientIDEditText.setText(id);
            flipper.showNext();
        }
    }

    /**
     * Checks patients ID.
     * Starts ExaminationActivity or returns to the launcher if PID is accepted
     */
    private void checkPid() {
        if (session.isValid()) {
            //TODO: Show that the app is working on something
            ArrayList<String> info = (ArrayList<String>) service.getPatientData(patientIDEditText.getText().toString());
            if (info != null) {
                if (returnAfter) {
                    Intent data = new Intent();
                    data.putStringArrayListExtra("patient", info);
                    setResult(RESULT_OK, data);
                    returnAfter = false;
                }else {
                    Intent i = new Intent(IdentifyPatientActivity.this, ExaminationActivity.class);
                    i.putStringArrayListExtra("info", info);
                    i.putStringArrayListExtra("chosen_images", incomingImages);
                    startActivity(i);
                }
                finish();
            } else Toast.makeText(getApplicationContext(), "Invalid ID", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getApplicationContext(), "Session timed out, or lost connection", Toast.LENGTH_LONG).show();
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
            if(scanResult.getFormatName().equals("QR_CODE")) {
                Log.d("APP", scanResult.toString());

                String[] patientData = scanResult.getContents().split(",");
                String patientSsn = patientData[0];
                String patientName = patientData[1];

                Log.d("APP", "Format: " + scanResult.getFormatName());
                Log.d("APP", "SSN: " + patientSsn);
                Log.d("APP", "Name: " + patientName);
            }
            else {
                Log.d("APP", "Invalid format");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        service.releaseService();
    }
}