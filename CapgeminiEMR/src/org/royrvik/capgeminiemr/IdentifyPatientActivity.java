package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by Joakim on 28.02.14.
 */
public class IdentifyPatientActivity extends Activity {

    private Button backButton, okButton;
    private ImageButton  manualButton, automaticButton;
    private EditText input;
    private TextView error;
    private ViewFlipper flipper;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identify);
        intent = getIntent();
        flipper = (ViewFlipper) findViewById(R.id.identifyFlipper);
        input = (EditText) findViewById(R.id.editText);
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
                input.setText("");
                error.setText("");
                flipper.showPrevious();
            }
        });

        okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!input.getText().toString().trim().isEmpty()) {
                    checkPid();
                }
                else {
                    error.setText("Invalid ID format.");
                }
            }
        });

    }

    private void checkPid() {
        //TODO: Validate the ID
        //TODO: Get patient info
        ArrayList<String> info = new ArrayList<String>();
        info.add(input.getText().toString());
        info.add("Frank Stangelberg");
        info.addAll(intent.getStringArrayListExtra("chosen_images"));

        Intent i = new Intent(IdentifyPatientActivity.this, ExaminationView.class);
        i.putStringArrayListExtra("info", info);
        startActivity(i);
    }
}
