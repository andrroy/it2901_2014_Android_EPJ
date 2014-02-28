package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;


public class TechnicalSetupActivity extends Activity {

    private TextView statusTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tech_setup);

        statusTextView = (TextView)findViewById(R.id.statusTextView);
        statusTextView.setText("Application is not currently set up.");
        statusTextView.setTextColor(Color.RED);

    }
}