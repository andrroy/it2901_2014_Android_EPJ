package org.royrvik.vscanlauncher;

import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.cengalabs.flatui.FlatUI;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class LauncherActivity extends ActionBarActivity {

    private static final String TAG = "APP";
    private static int RESULT_LOAD_IMAGE = 1;
    private static final String BROADCAST_CODE = "find a better code?";

    private Button newExaminationBtn, launchGatewayBtn, settingsBtn;

    private ReceiveMessages receiver = null;
    private ArrayList<String> selectedImagesPath;
    private ArrayList<String> patientData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.setDefaultTheme(FlatUI.ORANGE);
        setContentView(R.layout.main_test);

        // Actionbar style
        FlatUI.setActionBarTheme(this, FlatUI.DARK, false, true);
        getSupportActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(FlatUI.DARK, false));
        getActionBar().setTitle(Html.fromHtml("<font color=\"#f2f2f2\">" + getResources().getString(R.string.app_name)
                + "</font>"));

        selectedImagesPath = new ArrayList<String>();
        patientData = new ArrayList<String>();
        receiver = new ReceiveMessages();
        registerReceiver(receiver, new IntentFilter(BROADCAST_CODE));

        newExaminationBtn = (Button) findViewById(R.id.btn_newExamination);
        newExaminationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LauncherActivity.this);
                builder.setTitle("Identify patient?");
                builder.setMessage("Do you wish to identify the patient before the examination?");
                builder.setIcon(R.drawable.ic_add_patient);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startIdentifyPatient();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(LauncherActivity.this, ScannerActivity.class);
                        startActivity(i);
                    }
                });
                builder.show();
            }
        });

        launchGatewayBtn = (Button) findViewById(R.id.btn_gateway);
        launchGatewayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchHomeScreen();
            }
        });

        settingsBtn = (Button) findViewById(R.id.btn_settings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: DO nothing, we don't got any settings, lol.
            }
        });
    }

    /**
     * Launch straight to the home screen of Gateway.
     */

    private void launchHomeScreen() {
        new EMRLauncher(getApplicationContext()).start();
    }

    /**
     * Launches the main EMR application to identify a patient
     */
    private void startIdentifyPatient() {
        new EMRLauncher(getApplicationContext(), BROADCAST_CODE).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("APP:", "Launcher: OnActivityResult recieved!");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LauncherActivity.this);
        builder.setTitle("Exit application?");
        builder.setMessage("Do you wish to exit the Vscan application?");
        builder.setIcon(R.drawable.ic_alert);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private class ReceiveMessages extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> data = intent.getStringArrayListExtra("patient");
            if (data != null && data.size() > 0) {
                patientData = data;
                Intent i = new Intent(LauncherActivity.this, ScannerActivity.class);
                i.putStringArrayListExtra("patientData", patientData);
                startActivity(i);
            } else patientData.add("No ID available.");
            if (patientData.size() > 1) {
                Toast.makeText(LauncherActivity.this, "ID confirmed:\n" + patientData.get(2) + " " + patientData.get(3), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LauncherActivity.this, "No data received, check connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}