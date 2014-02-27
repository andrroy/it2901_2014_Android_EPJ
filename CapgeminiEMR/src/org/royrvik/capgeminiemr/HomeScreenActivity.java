package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class HomeScreenActivity extends Activity {

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
        listOfExaminations.add(new UltrasoundRowItem(incomingImages.get(0), "Test", 20, "lol"));
        listOfExaminations.add(new UltrasoundRowItem(incomingImages.get(0), "Test", 20, "lol2"));

        ultrasoundListView = (ListView) findViewById(R.id.ultrasoundImagesListView);
        ultrasoundListView.setAdapter(new HomeListAdapter(ctx, R.layout.row_list_item, listOfExaminations));




    }
}
