package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import org.royrvik.capgeminiemr.adapter.FullScreenImageAdapter;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.utils.HackyViewPager;


public class FullScreenViewActivity extends Activity {

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
