package org.royrvik.capgeminiemr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.adapter.HomescreenListAdapter;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.database.DatabaseHelper;
import org.royrvik.capgeminiemr.utils.SessionManager;

import java.util.ArrayList;

public class HomeScreenActivity extends SherlockActivity {

    private static final String TAG = "APP";

    private ListView homescreenListView;
    private Context context;
    private DatabaseHelper dbHelper;
    private ArrayList<Examination> listOfExaminations;
    private SessionManager session;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        context = this;

        dbHelper = new DatabaseHelper(this);

        //Getting the session
        session = new SessionManager(getApplicationContext());

        // Get all examinations in the database
        listOfExaminations = dbHelper.getAllExaminations();

        if(!session.isValid()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(HomeScreenActivity.this);
            dialog.setMessage("This information is not available in offline mode");
            dialog.setIcon(R.drawable.ic_info);
            dialog.setNeutralButton("OK", null);
            dialog.show();
        }
        else {
            homescreenListView = (ListView) findViewById(R.id.homeScreenListView);
            final HomescreenListAdapter homescreenListAdapter = new HomescreenListAdapter(context, R.layout.row_list_item_homescreen, listOfExaminations);
            homescreenListView.setAdapter(homescreenListAdapter);
            homescreenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Examination ex = (Examination)parent.getItemAtPosition(position);

                    // Build and show popup
                    AlertDialog.Builder dialog = new AlertDialog.Builder(HomeScreenActivity.this);
                    dialog.setTitle("Examination ID " + ex.getId());
                    StringBuilder infoString = new StringBuilder();
                    infoString.append("Name: " + ex.getPatientName() + "\n");
                    infoString.append("SSN: " + ex.getPatientSsn() + "\n");
                    infoString.append("Date: " + ex.getDate() + "\n");
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
                                    if(deleted)
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
                            i.putExtra("ex_id", ex.getId());
                            startActivity(i);
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
                Log.d("APP", "logout");
                session.logout();
                // Return to login
                Intent intent = new Intent(HomeScreenActivity.this, LoginActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.xml.menu_homescreen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }
}
