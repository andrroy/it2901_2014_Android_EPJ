package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;
import org.royrvik.capgeminiemr.adapter.FullScreenImageAdapter;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.utils.CustomViewPager;
import org.royrvik.capgeminiemr.utils.SessionManager;

public class FullScreenViewActivity extends Activity {

    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private Examination currentExamination;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        viewPager = (CustomViewPager) findViewById(R.id.pager);
        session = new SessionManager(getApplicationContext());

        Intent i = getIntent();
        currentExamination = i.getParcelableExtra("examination");

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("APP:", "onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("APP:", "onPageSelected");

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("APP:", "onPageScrollStateChanged");
            }
        });

        adapter = new FullScreenImageAdapter(this, currentExamination);
        viewPager.setAdapter(adapter);

    }

    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        updateSession();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSession();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    private void updateSession() {
        if (session.isValid()) {
            session.updateSession();
        }
    }
}
