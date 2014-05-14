package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.cengalabs.flatui.FlatUI;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.data.Examination;
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
    private ArrayList<String> incomingImages, examinationData;
    private boolean returnAfter = false;
    private SessionManager session;
    private RemoteServiceConnection service;
    private ProgressDialog pDialog;
    private Examination currentExamination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.setDefaultTheme(FlatUI.BLOOD);
        setContentView(R.layout.identify);

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

        // Easy way of figuring out where the activity was started from.
        if(i.hasExtra("examination")) {
            Log.d("APP:", "Identify: Examination is being edited");
            currentExamination = i.getParcelableExtra("examination");
        }else{
            Log.d("APP:", "Identify: Examination is about to be created");
            incomingImages = i.getStringArrayListExtra("chosen_images");
            examinationData = i.getStringArrayListExtra("examinationData");
            returnAfter = i.getBooleanExtra("return", false);
        }

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
                manualButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_editbtn));
                flipper.showNext();
            }
        });

        manualButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                manualButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_editbtn_down));
                return false;
            }
        });

        automaticButton = (ImageButton) findViewById(R.id.automaticButton);
        automaticButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                automaticButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera));
                IntentIntegrator integrator = new IntentIntegrator(IdentifyPatientActivity.this);
                integrator.initiateScan();
            }
        });

        automaticButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                automaticButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_down));
                return false;
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
                    Log.d("APP:", "Identify: Start checkPinTask");
                    new CheckPidTask().execute(patientIDEditText.getText().toString());

                } else
                    Toast.makeText(getApplicationContext(), "Please enter a social security number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Checks patients ID.
     * Starts ExaminationActivity or returns to the launcher if PID is accepted.
     */
    private class CheckPidTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Looking up patient...");

            ArrayList<String> info = new ArrayList<String>();
            if (session.isValid()) {
                Log.d("APP:", "Identify: Session is valid");
                ArrayList<String> auth = ((EMRApplication) getApplicationContext()).getDepartmentAuth();
                info = (ArrayList<String>) service.getPatientData(params[0], auth.get(0), auth.get(1));

                // If service failed to fetch data
                if (info == null || !Boolean.valueOf(info.get(0))) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error: Invalid ID", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else { // If service succeded
                    Log.d("APP:", "Identify: testing cases");
                    if (returnAfter) {
                        returnToVscan(info);

                        // Updates existing examination with new name and ID
                    } else if (currentExamination != null) {
                        Log.d("APP:", "Identify success: Setting new name and returning to ExaminationActivity...");
                        currentExamination.setPatientSsn(info.get(1));
                        currentExamination.setPatientFirstName(info.get(2));
                        currentExamination.setPatientLastName(info.get(3));
                        returnToExamination();
                    }
                    // This creates a new examination and starts ExaminationActivity
                    else if (currentExamination == null) {
                        createNewExamination(info);
                    }
                }
            }else { // If session is not valid set patient data
                Log.d("APP:", "Identify fail: Session is not valid. Clearing name and set SSN...");
                // If the validation fails, clear name and set ssn to what user wrote.
                info.add("");
                info.add(patientIDEditText.getText().toString());
                if(returnAfter)
                    returnToVscan(info);
                else if(currentExamination != null){
                    currentExamination.setPatientSsn(patientIDEditText.getText().toString());
                    currentExamination.setPatientFirstName("");
                    currentExamination.setPatientLastName("");
                    returnToExamination();
                }else{
                    createNewExamination(info);
                }
                finish();
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(IdentifyPatientActivity.this);
            pDialog.setMessage("Looking up patient...");
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            if(!(pDialog == null))
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
                Crouton.makeText(IdentifyPatientActivity.this, "Invalid format", Style.ALERT).show();
            }
        }
    }

    private void createNewExamination(ArrayList<String> info){
        Intent i = new Intent(IdentifyPatientActivity.this, ExaminationActivity.class);
        i.putStringArrayListExtra("patientData", info);
        i.putStringArrayListExtra("chosen_images", incomingImages);
        i.putStringArrayListExtra("examinationData", examinationData);
        startActivity(i);
        finish();
    }

    private void returnToVscan(ArrayList<String> info){
        Intent data = new Intent();
        data.putStringArrayListExtra("patient", info);
        setResult(RESULT_OK, data);
        returnAfter = false;
        finish();
    }

    /**
     * Returns to ExaminationActivity
     * Should only be called when editing SSN of existing examination
     */

    private void returnToExamination(){
        Log.d("APP", "IdentifyPatient: Returning to Examination...");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("examination", currentExamination);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
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
    public void onBackPressed(){
        Log.d("APP:", "Identify: Back button pressed");
        // If the user presses back when editing an existing examination
        if (flipper.getDisplayedChild() > 0)
            flipper.showPrevious();
        else if(currentExamination != null){
            returnToExamination();
        }else{
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
