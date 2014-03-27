package org.royrvik.capgeminiemr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import org.royrvik.capgeminiemr.utils.SessionManager;

import java.util.ArrayList;


public class IdentifyPatientActivity extends SherlockActivity {

    private Button backButton, okButton;
    private ImageButton  manualButton, automaticButton;
    private EditText patientIDEditText;
    private TextView error;
    private ViewFlipper flipper;
    private ArrayList<String> incomingImages;
    private Intent intent;
    private boolean returnAfter = false;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identify);

        //Getting the session
        session = new SessionManager(getApplicationContext());

        //Actionbarsherlock back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // get intent from last activity
        Intent i = getIntent();
        incomingImages = i.getStringArrayListExtra("chosen_images");
        String id = i.getStringExtra("id");
        returnAfter = i.getBooleanExtra("return", false);

        flipper = (ViewFlipper) findViewById(R.id.identifyFlipper);
        patientIDEditText = (EditText) findViewById(R.id.editText);
        error = (TextView) findViewById(R.id.errorText);


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
                //TODO: Not yet implemented
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
                else {
                    error.setText("Invalid ID format.");
                }
            }
        });
        //If activity was started with id
        if (id != null) { //Maybe more tests is needed
            patientIDEditText.setText(id);
            checkPid();
        }
    }

    /**
     * Checks patients ID.
     * Starts ExaminationActivity or returns to the launcher if PID is accepted
     */
    private void checkPid() {
        //TODO: Validate the ID
        //TODO: Show that the app is working on something
        //TODO: Get patient info
        if (true /*The ID was validated*/) {
            ArrayList<String> info = new ArrayList<String>();
            info.add(patientIDEditText.getText().toString());
            info.add("Frank Stangelberg"); //For testing only

            if (returnAfter) {
                Intent data = new Intent();
                data.putStringArrayListExtra("patient", info);
                setResult(RESULT_OK, data);
                returnAfter = false;
                finish();
            }
            else {
                Intent i = new Intent(IdentifyPatientActivity.this, ExaminationActivity.class);
                i.putStringArrayListExtra("info", info);
                i.putStringArrayListExtra("chosen_images", incomingImages);
                startActivity(i);
                finish();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Invalid ID", Toast.LENGTH_SHORT).show();
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
                session.logout();
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        session.logout();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}
