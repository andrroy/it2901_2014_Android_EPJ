package org.royrvik.capgeminiemr;

/**
 * Created by rikardeide on 20/4/14.
 */

import org.royrvik.capgeminiemr.adapter.FullScreenImageAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

public class FullScreenViewActivity extends Activity{

    // private Utils utils - Need a way to put URIs
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private ArrayList<String> incomingImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        viewPager = (ViewPager) findViewById(R.id.pager);

        //utils = new Utils(getApplicationContext()); Finn ut hva som er vanlig å gjøre med context - brukes for å sende state til andre klasser.

        Intent i = getIntent();
        incomingImages = i.getStringArrayListExtra("ex_images");

        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, incomingImages);

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(0);
    }

}
