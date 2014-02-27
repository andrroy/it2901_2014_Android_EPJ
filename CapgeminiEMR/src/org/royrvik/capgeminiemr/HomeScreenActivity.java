package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import org.royrvik.capgeminiemr.adapter.HomescreenListAdapter;
import org.royrvik.capgeminiemr.adapter.UltrasoundRowItem;
import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends Activity {

    private static final String TAG = "APP";

    private ListView ultrasoundListView;
    private Context context;
    private ArrayList<String> incomingImages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        context = this;

        // Get images from previous Activity
        Intent intent = getIntent();
        incomingImages = intent.getStringArrayListExtra("chosen_images");

        List<UltrasoundRowItem> listOfExaminations = new ArrayList<UltrasoundRowItem>();
        for (int i = 0; i < incomingImages.size(); i++) {
            listOfExaminations.add(new UltrasoundRowItem(incomingImages.get(i), i, "lol" + i));
        }

        ultrasoundListView = (ListView) findViewById(R.id.ultrasoundImagesListView);
        ultrasoundListView.setAdapter(new HomescreenListAdapter(context, R.layout.row_list_item, listOfExaminations));

    }
}
