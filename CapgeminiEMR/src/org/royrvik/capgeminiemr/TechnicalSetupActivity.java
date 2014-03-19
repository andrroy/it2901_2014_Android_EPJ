package org.royrvik.capgeminiemr;

import android.graphics.Color;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class TechnicalSetupActivity extends SherlockActivity {

    private TextView statusTextView;
    private Button getConfigButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tech_setup);

        //Actionbarsherlock back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        statusTextView = (TextView) findViewById(R.id.statusTextView);
        //Should only be displayed if app is indeed not yet set up
        statusTextView.setText("Application is not currently set up.");
        statusTextView.setTextColor(Color.RED);

        getConfigButton = (Button) findViewById(R.id.getConfigButton);

        getConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    //If app is already set up, the user should be notified, and the database should be dropped
                    statusTextView.setText("Downloading and importing settings..");
                    //Settings must be added to sharedPreferences
                    //String xml = XmlParser.parse("http://folk.ntnu.no/andrroy/settings.xml");
                    statusTextView.setText("Settings imported");
                } catch(NetworkOnMainThreadException e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back button clicked. Exit activity and open previous in activity stack
                finish();
                break;
        }
        return true;
    }

}