package org.royrvik.capgeminiemr;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.MenuItem;
import com.cengalabs.flatui.FlatUI;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class SettingsActivity extends PreferenceActivity {


    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        // Actionbar style
        FlatUI.setActionBarTheme(this, FlatUI.DARK, false, true);
        getActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(FlatUI.DARK, false));
        getActionBar().setTitle(Html.fromHtml("<font color=\"#f2f2f2\"> <em><b>" + getResources().getString(R.string.app_name)
                + "</b></em></font>"));

        //Actionbar back button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);

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
