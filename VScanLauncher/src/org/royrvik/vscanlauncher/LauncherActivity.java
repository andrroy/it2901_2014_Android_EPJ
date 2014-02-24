package org.royrvik.vscanlauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LauncherActivity extends Activity {

    private Button launchOtherAppButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        launchOtherAppButton = (Button) findViewById(R.id.launchOtherAppButton);
        launchOtherAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOtherApplication();
            }
        });

    }

    /**
     * Launches the main EMR application. Currently
     */
    private void startOtherApplication() {

        Intent startIntent = getPackageManager().getLaunchIntentForPackage("org.royrvik.capgeminiemr");
        startActivity(startIntent);

    }
}