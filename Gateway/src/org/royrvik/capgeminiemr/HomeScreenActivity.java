package org.royrvik.capgeminiemr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.cengalabs.flatui.FlatUI;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.adapter.HomescreenListAdapter;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.database.DatabaseHelper;
import org.royrvik.capgeminiemr.utils.SessionManager;
import org.royrvik.capgeminiemr.utils.Utils;

import java.util.ArrayList;

public class HomeScreenActivity extends ActionBarActivity {

    private ListView homescreenListView;
    private DatabaseHelper dbHelper;
    private ArrayList<Examination> listOfExaminations;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        // Actionbar style
        FlatUI.setActionBarTheme(this, FlatUI.DARK, false, true);
        getSupportActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(FlatUI.DARK, false));
        getActionBar().setTitle(Html.fromHtml("<font color=\"#f2f2f2\"> <em><b>" + getResources().getString(R.string.app_name)
                + "</b></em></font>"));


        //Alert dialog used throughout the view
        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeScreenActivity.this);


        Intent i = getIntent();

        //Alert dialog that shows status of upload if the users has just uploaded an examination
        if(i.hasExtra("upload_success") || i.hasExtra("upload_fail")){
            if (i.hasExtra("upload_success")) {
                dialog.setMessage("Examination was successfully uploaded");
            }
            else {
                dialog.setMessage("Unable to upload examination. Please try again.");
            }
            dialog.setIcon(R.drawable.ic_info);
            dialog.setNeutralButton("OK", null);
            dialog.show();
        }

        //Getting the session
        session = new SessionManager(getApplicationContext());

        dbHelper = DatabaseHelper.getInstance(this, session.getDatabaseInfo());

        // Get all examinations in the database
        listOfExaminations = dbHelper.getAllExaminations();
        if (listOfExaminations.isEmpty()) {
            TextView emptyListTextView = (TextView) findViewById(R.id.emptyListTextView);
            emptyListTextView.setVisibility(View.VISIBLE);
        }

        if (!session.isValid()) {
            dialog.setTitle(getResources().getString(R.string.homescreen));
            dialog.setMessage(getResources().getString(R.string.err_not_available));
            dialog.setIcon(R.drawable.ic_info);
            dialog.setNeutralButton("OK", null);
            dialog.show();
        } else {
            homescreenListView = (ListView) findViewById(R.id.homeScreenListView);
            final HomescreenListAdapter homescreenListAdapter = new HomescreenListAdapter(this, R.layout.row_list_item_homescreen, listOfExaminations);
            homescreenListView.setAdapter(homescreenListAdapter);
            homescreenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Examination ex = (Examination) parent.getItemAtPosition(position);

                    // Build and show popup
                    AlertDialog.Builder dialog = new AlertDialog.Builder(HomeScreenActivity.this);
                    dialog.setTitle("Examination " + ex.getExaminationNumber());
                    StringBuilder infoString = new StringBuilder();

                    if(ex.getPatientFirstName().equals(""))
                        infoString.append("Name: " + getResources().getString(R.string.not_found)+"\n");
                    else
                        infoString.append("Name: " + ex.getPatientFirstName() + " " + ex.getPatientLastName() + "\n");

                    infoString.append("SSN: " + ex.getPatientSsn() + "\n");
                    infoString.append("Date: " + Utils.formattedDate(ex.getExaminationTime()) + "\n");
                    infoString.append("Number of images: " + ex.getUltrasoundImages().size() + "\n");
                    dialog.setMessage(infoString);
                    dialog.setIcon(R.drawable.ic_info);

                    dialog.setPositiveButton("OK", null);
                    dialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Confirmation dialog
                            AlertDialog.Builder confirmDialog = new AlertDialog.Builder(HomeScreenActivity.this);
                            confirmDialog.setTitle("Confirm");
                            confirmDialog.setMessage("Are you sure you want to delete this examination?");
                            confirmDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Delete the Examination from the database
                                    boolean deleted = dbHelper.deleteExamination(ex.getId());
                                    if (deleted)
                                        Crouton.makeText(HomeScreenActivity.this, "Examination deleted", Style.CONFIRM).show();

                                    // Update data set and notify the adapter of the changes
                                    listOfExaminations.clear();
                                    listOfExaminations.addAll(dbHelper.getAllExaminations());
                                    homescreenListAdapter.notifyDataSetChanged();
                                    homescreenListView.refreshDrawableState();
                                }
                            });
                            confirmDialog.setNegativeButton("No", null);
                            confirmDialog.show();

                        }
                    });
                    dialog.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Start ExaminationActivity
                            Intent i = new Intent(HomeScreenActivity.this, ExaminationActivity.class);
                            i.putExtra("examination", ex);
                            startActivity(i);
                            finish();
                        }
                    });
                    dialog.show();

                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_button:
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this);
                builder.setTitle("Log out?");
                builder.setMessage("Do you wish to end your current session and log out?");
                builder.setIcon(R.drawable.ic_alert);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        session.logout();
                        dbHelper.logout();
                        finish();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_homescreen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSession();
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this);
        builder.setTitle("Exit application");
        builder.setMessage("Do you wish to return to the Vscan application without logging out?");
        builder.setIcon(R.drawable.ic_alert);
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("Log out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                session.logout();
                dbHelper.logout();
                finish();
            }
        });
        builder.show();
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
    protected void onPause(){
        super.onPause();
        finish();
    }

}
