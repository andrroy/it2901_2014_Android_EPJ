package org.royrvik.capgeminiemr;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import org.royrvik.capgeminiemr.database.DatabaseHelper;
import org.royrvik.capgeminiemr.utils.Validator;
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

        //Sets status message based on whether app is configured or not
        if(globalApp.hasSettingsConfigured()){
            statusTextView.setText("Application is already configured.");
            statusTextView.setTextColor(Color.GREEN);
        }
        else{
            statusTextView.setText("Application is not currently set up.");
            statusTextView.setTextColor(Color.RED);
        }


        pathToXmlEditText = (EditText) findViewById(R.id.pathToSettingsEditText);
        getConfigButton = (Button) findViewById(R.id.getConfigButton);

        getConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                    HashMap<String, String> settingsHashMap = XmlParser.parse(pathToXmlEditText.getText().toString());

                    if(globalApp.hasSettingsConfigured()){

                        //Displays alertDialog notifying user that examinations will be lost if user proceeds with setup
                        new AlertDialog.Builder(TechnicalSetupActivity.this)
                                .setTitle("Delete local examinations")
                                .setMessage("This procedure will delete all examinations waiting to be uploaded. Are you sure you want to do this?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                                        dbHelper.deleteAllExaminations();
                                        Log.d("APP", "DELETING OMG");
                                    }
                                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing
                            }
                        }).show();
                    }

                    if(Validator.validateSettings(settingsHashMap)){
                        addSettingsToSharedPreferences(settingsHashMap);
                        statusTextView.setText("Settings imported");
                        statusTextView.setTextColor(Color.GREEN);
                        Crouton.makeText(TechnicalSetupActivity.this, "Settings successfully imported.", Style.CONFIRM).show();
                    } else{
                        statusTextView.setText("Settings invalid");
                        statusTextView.setTextColor(Color.RED);
                        Crouton.makeText(TechnicalSetupActivity.this, "Settings was not imported.", Style.ALERT).show();
                    }

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
//        Log.d("APP", "= PackageName: " + globalApp.getSettingsPackageName());
//        Log.d("APP", "= PackageLocation: " + globalApp.getSettingsPackageLocation());
//        Log.d("APP", "= PackageServerPort: " + globalApp.getSettingsPackageServerPort());
//        Log.d("APP", "= HospitalServerAddress_ " + globalApp.getSettingsHospitalServerAddress());
//        Log.d("APP", "= HospitalServerProtocol: " + globalApp.getSettingsHospitalServerProtocol());
//        Log.d("APP", "= HospitalServerPort: " + globalApp.getSettingsHospitalServerPort());
//        Log.d("APP", "= AuthenticationProtocol: " + globalApp.getSettingsAuthenticationProtocol());
//        Log.d("APP", "= AuthenticationServer: " + globalApp.getSettingsAuthenticationServerAddress());
//        Log.d("APP", "= AuthenticationServerPort: " + globalApp.getSettingsAuthenticationServerPort());
//        Log.d("APP", "= LDAP UserID: " + globalApp.getSettingsLDAPUserID());
//        Log.d("APP", "= LDAP OU: " + globalApp.getSettingsLDAPOU());
//        Log.d("APP", "= LDAP DC: " + globalApp.getSettingsLDAPDC());

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