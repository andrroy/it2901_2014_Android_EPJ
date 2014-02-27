package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class HomeScreenActivity extends Activity {

    private static final String TAG = "APP";

    private ListView ultrasoundListView;
    private Context ctx;
    private ArrayList<String> incomingImages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        ctx = this;

        // Get images from previous Activity
        Intent i = getIntent();
        incomingImages = i.getStringArrayListExtra("chosen_images");

        List<UltrasoundRowItem> listOfExaminations = new ArrayList<UltrasoundRowItem>();
        listOfExaminations.add(new UltrasoundRowItem(incomingImages.get(0), 101, "lol"));
        listOfExaminations.add(new UltrasoundRowItem(incomingImages.get(0), 102, "lol2"));

        Log.w(TAG, incomingImages.get(0));

        ultrasoundListView = (ListView) findViewById(R.id.ultrasoundImagesListView);
        ultrasoundListView.setAdapter(new HomeListAdapter(ctx, R.layout.row_list_item, listOfExaminations));


    }
}
