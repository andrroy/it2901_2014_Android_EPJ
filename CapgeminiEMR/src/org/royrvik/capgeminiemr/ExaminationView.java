package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Joakim on 27.02.14.
 */
public class ExaminationView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.examination);

        Intent i = getIntent();

    }
}
