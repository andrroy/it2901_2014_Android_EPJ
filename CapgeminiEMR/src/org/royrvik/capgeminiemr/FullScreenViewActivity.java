package org.royrvik.capgeminiemr;

/**
 * Created by rikardeide on 20/4/14.
 */

import android.support.v4.view.PagerAdapter;
import android.view.View;
import org.royrvik.capgeminiemr.adapter.FullScreenImageAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.utils.HackyViewPager;


public class FullScreenViewActivity extends Activity {

    // private Utils utils - Need a way to put URIs
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private Examination currentExamination;

    private static final String ISLOCKED_ARG = "isLocked";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        viewPager = (HackyViewPager) findViewById(R.id.pager);

        Intent i = getIntent();
        currentExamination = i.getParcelableExtra("examination");

        adapter = new FullScreenImageAdapter(this, currentExamination);
        viewPager.setAdapter(adapter);
    }
}