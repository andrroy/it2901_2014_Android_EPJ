package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class HomeScreenActivity extends Activity {

    private ListView ultrasoundListView;
    private Context ctx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        ultrasoundListView = (ListView) findViewById(R.id.ultrasoundImagesListView);

        List<UltrasoundRowItem> listOfExaminations = new ArrayList<UltrasoundRowItem>();
        

    }
}
