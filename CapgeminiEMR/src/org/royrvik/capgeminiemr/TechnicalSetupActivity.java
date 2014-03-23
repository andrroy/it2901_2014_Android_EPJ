package org.royrvik.capgeminiemr;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.utils.XmlParser;

import java.util.HashMap;

public class TechnicalSetupActivity extends SherlockActivity {

    private TextView statusTextView;
    private Button getConfigButton;
    private EMRApplication globalApp;
    private EditText pathToXmlEditText;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tech_setup);

        //Actionbarsherlock back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Initialize Application (SharedPreferences controller)
        globalApp = (EMRApplication) getApplicationContext();

        statusTextView = (TextView) findViewById(R.id.statusTextView);
        //Should only be displayed if app is indeed not yet set up
        statusTextView.setText("Application is not currently set up.");
        statusTextView.setTextColor(Color.RED);

        pathToXmlEditText = (EditText) findViewById(R.id.pathToSettingsEditText);

        getConfigButton = (Button) findViewById(R.id.getConfigButton);

        getConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // If app is already set up, the user should be notified and the database should be dropped

                    // http://folk.ntnu.no/andrroy/settings.xml
                    addSettingsToSharedPreferences(XmlParser.parse(pathToXmlEditText.getText().toString()));

                    statusTextView.setText("Settings imported");
                    statusTextView.setTextColor(Color.GREEN);
                    Crouton.makeText(TechnicalSetupActivity.this, "Settings successfully imported.", Style.CONFIRM).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Crouton.makeText(TechnicalSetupActivity.this, "Problem...", Style.ALERT).show();
                    // TODO: Handle exceptions better...
                }
            }
        });

    }

    private void addSettingsToSharedPreferences(HashMap<String, String> settingsHashMap) {

        globalApp.setExternalPackageSettings(settingsHashMap);
        Log.d("APP", globalApp.getSettingsHospitalServer());

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