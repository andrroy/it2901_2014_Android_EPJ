package org.royrvik.capgeminiemr;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import com.cengalabs.flatui.FlatUI;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import org.royrvik.capgeminiemr.adapter.CurrentSetupListAdapter;
import org.royrvik.capgeminiemr.data.SettingsItem;

import java.util.ArrayList;
import java.util.Map;

public class CurrentSetupActivity extends ActionBarActivity {

    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewsetup);

        // Actionbar style
        FlatUI.setActionBarTheme(this, FlatUI.DARK, false, true);
        getSupportActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(FlatUI.DARK, false));
        getActionBar().setTitle(Html.fromHtml("<font color=\"#f2f2f2\"> <em><b>" + getResources().getString(R.string.app_name)
                + "</b></em></font>"));

        Context context = this;

        EMRApplication globalApp = (EMRApplication) getApplicationContext();

        //Actionbar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backbtn);

        // Get current settings from Application class
        ArrayList<SettingsItem> listOfSettings = buildData(globalApp.getAllPreferences());

        ListView currentSetupListView = (ListView) findViewById(R.id.currentSetupListView);
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

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}