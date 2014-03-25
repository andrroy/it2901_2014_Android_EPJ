package org.royrvik.capgeminiemr;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import org.royrvik.capgeminiemr.adapter.CurrentSetupListAdapter;
import org.royrvik.capgeminiemr.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CurrentSetupActivity extends SherlockActivity {

    private EMRApplication globalApp;
    private ListView currentSetupListView;
    private Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewsetup);

        context = this;

        globalApp = (EMRApplication) getApplicationContext();

        //Actionbarsherlock back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get current settings from Application class
        ArrayList<ArrayList<String>> keyValueList = buildData(globalApp.getAllPreferences());

        currentSetupListView = (ListView) findViewById(R.id.currentSetupListView);
        currentSetupListView.setAdapter(new CurrentSetupListAdapter(context, R.layout.row_list_item_currentsetup, keyValueList));


    }

    public ArrayList<ArrayList<String>> buildData(Map<String, ?> preferenceHashMap) {

        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();

        for (String key : preferenceHashMap.keySet()) {
            keys.add(key);
            values.add((String) preferenceHashMap.get(key));
        }

        ArrayList<ArrayList<String>> keyValueList = new ArrayList<ArrayList<String>>();
        keyValueList.add(keys);
        keyValueList.add(values);

        return keyValueList;
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
