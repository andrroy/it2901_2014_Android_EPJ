package org.royrvik.capgeminiemr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import java.util.ArrayList;


public class IdentifyPatientActivity extends SherlockActivity {

    private Button backButton, okButton;
    private ImageButton  manualButton, automaticButton;
    private EditText patientIDEditText;
    private TextView error;
    private ViewFlipper flipper;
    private ArrayList<String> incomingImages;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identify);

        //Actionbarsherlock back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // get intent from last activity
        Intent i = getIntent();
        incomingImages = i.getStringArrayListExtra("chosen_images");

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
                if (!patientIDEditText.getText().toString().trim().isEmpty()) {
                    checkPid();
                }
                else {
                    error.setText("Invalid ID format.");
                }
            }
        });

    }

    /**
     * Checks patients ID.
     * Starts ExaminationActivity if PID is accepted
     */
    private void checkPid() {
        //TODO: Validate the ID
        //TODO: Get patient info
        ArrayList<String> info = new ArrayList<String>();
        info.add(patientIDEditText.getText().toString());
        info.add("Frank Stangelberg");

        Intent i = new Intent(IdentifyPatientActivity.this, ExaminationActivity.class);
        i.putStringArrayListExtra("info", info);
        i.putStringArrayListExtra("chosen_images", incomingImages);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back button clicked. Exit activity and open previous in activity stack
                finish();
                break;
        }
        return true;
    }
}