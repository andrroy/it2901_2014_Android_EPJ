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
    private Button tagButton;

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

        initFirstViewElements();
    }

    private void initFirstViewElements(){
        tagButton = (Button) findViewById(R.id.btnTag);

        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagButton.setBackgroundResource(R.drawable.ic_tags);
                Log.d("APP:", "Tagbutton clicked!!");
            }
        });

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String comment) {
        // Saves comment to image 0 for testing purposes!
        Log.d("APP:", "Positive button clicked!");
        currentExamination.getUltrasoundImages().get(0).setComment(comment);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d("APP:", "Negative button clicked!");
    }
}