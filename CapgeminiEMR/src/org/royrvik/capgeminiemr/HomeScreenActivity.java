package org.royrvik.capgeminiemr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.adapter.HomescreenListAdapter;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.database.DatabaseHelper;

import java.util.ArrayList;

public class HomeScreenActivity extends SherlockActivity {

    private static final String TAG = "APP";

    private ListView homescreenListView;
    private Context context;
    private DatabaseHelper dbHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        context = this;

        dbHelper = new DatabaseHelper(this);

        //Actionbarsherlock back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get all examinations in the database
        ArrayList<Examination> listOfExaminations = dbHelper.getAllExaminations();

        homescreenListView = (ListView) findViewById(R.id.homeScreenListView);
        homescreenListView.setAdapter(new HomescreenListAdapter(context, R.layout.row_list_item_homescreen, listOfExaminations));
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

                dialog.setPositiveButton("OK", null);
                dialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete the Examination from the database
                        boolean deleted = dbHelper.deleteExamination(ex.getId());
                        if(deleted)
                            Crouton.makeText(HomeScreenActivity.this, "Examination deleted", Style.CONFIRM).show();
                        else
                            Crouton.makeText(HomeScreenActivity.this, "Examination not found in the database", Style.CONFIRM).show();
                    }
                });
                dialog.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Go
                    }
                });
                dialog.show();


            }
        });

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
