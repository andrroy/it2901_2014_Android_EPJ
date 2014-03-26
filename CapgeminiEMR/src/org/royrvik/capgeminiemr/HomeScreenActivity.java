package org.royrvik.capgeminiemr;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
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
