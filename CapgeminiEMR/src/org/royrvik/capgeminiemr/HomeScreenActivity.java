package org.royrvik.capgeminiemr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import org.royrvik.capgeminiemr.adapter.HomescreenListAdapter;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.data.UltrasoundImage;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends SherlockActivity {

    private static final String TAG = "APP";

    private ListView ultrasoundListView;
    private Context context;
    private ArrayList<String> incomingImages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
//
        context = this;

        //Actionbarsherlock back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        // Get images from previous Activity
        Intent intent = getIntent();
        incomingImages = intent.getStringArrayListExtra("chosen_images");



        /*List<Examination> listOfExaminations = new ArrayList<Examination>();
        for (int i = 0; i < incomingImages.size(); i++) {
            listOfExaminations.add(new Examination(1, "Navn"));
        }

        ultrasoundListView = (ListView) findViewById(R.id.ultrasoundImagesListView);
        ultrasoundListView.setAdapter(new HomescreenListAdapter(context, R.layout.row_list_item_homescreen, listOfExaminations));*/

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
