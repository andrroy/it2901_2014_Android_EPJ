package org.royrvik.capgeminiemr;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import org.royrvik.capgeminiemr.adapter.CurrentSetupListAdapter;
import org.royrvik.capgeminiemr.data.SettingsItem;

import java.util.ArrayList;
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
        ArrayList<SettingsItem> listOfSettings = buildData(globalApp.getAllPreferences());

        currentSetupListView = (ListView) findViewById(R.id.currentSetupListView);
        currentSetupListView.setAdapter(new CurrentSetupListAdapter(context, R.layout.row_list_item_currentsetup, listOfSettings));

    }

    /**
     * Builds the dataset for the adapter to use
     *
     * @param preferenceHashMap Hashmap from EMRApplication
     * @return ArrayList of all settings saved in SharedPreferences, each represented as SettingsItem
     */
    public ArrayList<SettingsItem> buildData(Map<String, ?> preferenceHashMap) {

        ArrayList<SettingsItem> settingsItemList = new ArrayList<SettingsItem>();

        for (String key : preferenceHashMap.keySet()) {
            settingsItemList.add(new SettingsItem(key, (String) preferenceHashMap.get(key)));
        }

        return settingsItemList;
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

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }
}
