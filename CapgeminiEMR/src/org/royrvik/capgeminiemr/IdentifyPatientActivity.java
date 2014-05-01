package org.royrvik.capgeminiemr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.cengalabs.flatui.FlatUI;
import org.royrvik.capgeminiemr.database.DatabaseHelper;
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
    private DatabaseHelper dbHelper;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.setDefaultTheme(FlatUI.BLOOD);
        setContentView(R.layout.identify);

        dbHelper = new DatabaseHelper(this);

        // Actionbar style
        FlatUI.setActionBarTheme(this, FlatUI.DARK, false, true);
        getSupportActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(FlatUI.DARK, false));
        getActionBar().setTitle(Html.fromHtml("<font color=\"#f2f2f2\">" + getResources().getString(R.string.app_name)
                + "</font>"));

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
                    new CheckPidTask().execute(patientIDEditText.getText().toString());

                } else
                    Toast.makeText(getApplicationContext(), "Please enter a social security number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Checks patients ID.
     * Starts ExaminationActivity or returns to the launcher if PID is accepted.
     *
     */
    private class CheckPidTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Working...");

            ArrayList<String> info = new ArrayList<String>();
            if (session.isValid()) {
                ArrayList<String> auth = dbHelper.getDepartmentAuth();
                info = (ArrayList<String>) service.getPatientData(params[0], auth.get(0), auth.get(1));
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
            }
            else {
                // Run Toast on UI thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Invalid ID", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(IdentifyPatientActivity.this);
            pDialog.setMessage("Working...");
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
        }

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

        if (scanResult.getContents() != null) {
            if (scanResult.getFormatName().equals("QR_CODE")) {
                String ssn = scanResult.getContents();
                new CheckPidTask().execute(ssn);

            } else if (scanResult.getFormatName().equals("CODE_128")) {
                String ssn = scanResult.getContents();
                new CheckPidTask().execute(ssn);
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

    @Override
    protected void onStop() {
        super.onStop();
        if(pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
