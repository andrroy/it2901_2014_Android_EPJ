package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

/**
 * Created by Joakim on 28.02.14.
 */
public class IdentifyPatientActivity extends Activity {

    private Button backButton, okButton;
    private ImageButton  manualButton, automaticButton;
    private EditText input;
    private TextView error;
    private ViewFlipper flipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identify);
        flipper = (ViewFlipper) findViewById(R.id.viewFlipper);
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
                System.out.println("THIS IS THE TEXT: "+input.getText());
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
        //TODO: Start next view with the gathered information
    }
}
