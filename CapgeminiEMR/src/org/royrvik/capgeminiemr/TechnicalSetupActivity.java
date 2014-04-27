package org.royrvik.capgeminiemr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.database.DatabaseHelper;
import org.royrvik.capgeminiemr.preferences.Validator;
import org.royrvik.capgeminiemr.preferences.XmlParser;

import java.util.HashMap;

public class TechnicalSetupActivity extends ActionBarActivity {

    private TextView statusTextView;
    private EditText pathToXmlEditText;
    private Button confirmButton;

    private EMRApplication globalApp;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tech_setup);

        //ActionbarSherlock back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Initialize Application (SharedPreferences controller)
        globalApp = (EMRApplication) getApplicationContext();

        statusTextView = (TextView) findViewById(R.id.statusTextView);

        //Sets status message based on whether app is configured or not
        if (globalApp.hasSettingsConfigured()) setStatusText("Application is already configured.", Color.GREEN);
        else setStatusText("Application is not currently set up.", Color.RED);

        pathToXmlEditText = (EditText) findViewById(R.id.pathToSettingsEditText);
        Button getConfigButton = (Button) findViewById(R.id.getConfigButton);

        getConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HashMap<String, String> settingsHashMap = XmlParser.parse(pathToXmlEditText.getText().toString());

                    //If settings are already configured, process settings.xml but give warning that data will be deleted
                    //Else, just process settings.xml
                    if (globalApp.hasSettingsConfigured()) processUserRequestWithWarning(settingsHashMap);
                    else processSettings(settingsHashMap);

                } catch (Exception e) {
                    e.printStackTrace();
                    Crouton.makeText(TechnicalSetupActivity.this, "Invalid source.", Style.ALERT).show();
                }
            }
        });

        confirmButton = (Button) findViewById(R.id.techSetupConfirmButton);
        if (!globalApp.hasSettingsConfigured()) {
            confirmButton.setVisibility(View.GONE);
        }
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TechnicalSetupActivity.this, TechDepartmentActivity.class);
                i.putExtra("init", true);
                startActivity(i);
                finish();
            }
        });
    }


    /**
     * @param settingsHashMap -
     */
    private void addSettingsToSharedPreferences(HashMap<String, String> settingsHashMap) {

        globalApp.setExternalPackageSettings(settingsHashMap);
    }

    /**
     * Runs validation method, and saves to shared Preferences if settings are valid.
     * Also outputs relevant information to user.
     *
     * @param settingsHashMap -
     */
    private void processSettings(HashMap<String, String> settingsHashMap) {

        if (Validator.validateSettings(settingsHashMap)) {
            addSettingsToSharedPreferences(settingsHashMap);
            setStatusText("Settings imported", Color.GREEN);
            confirmButton.setVisibility(View.VISIBLE);
            Crouton.makeText(TechnicalSetupActivity.this, "Settings successfully imported.", Style.CONFIRM).show();
        } else {
            setStatusText("Settings invalid", Color.RED);
            Crouton.makeText(TechnicalSetupActivity.this, "Settings was not imported.", Style.ALERT).show();
        }
    }

    /**
     * @param settingsHashMap -
     */
    private void processUserRequestWithWarning(final HashMap<String, String> settingsHashMap) {

        //Displays alertDialog notifying user that examinations will be lost if user proceeds with setup
        new AlertDialog.Builder(TechnicalSetupActivity.this)
                .setTitle("Delete local examinations")
                .setMessage("This procedure will delete all examinations waiting to be uploaded. Are you sure you want to do this?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                        dbHelper.deleteAllExaminations();
                        processSettings(settingsHashMap);
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing
            }
        }).show();
    }

    /**
     * @param text  the text to show
     * @param color the color of the text
     */
    private void setStatusText(String text, int color) {
        statusTextView.setText(text);
        statusTextView.setTextColor(color);
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

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }

}