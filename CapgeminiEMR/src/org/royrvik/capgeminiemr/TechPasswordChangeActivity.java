package org.royrvik.capgeminiemr;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.database.DatabaseHelper;

/**
 * Created by Joakim.
 */
public class TechPasswordChangeActivity extends ActionBarActivity {

    private EditText oldPw, newPw, repPw;
    private DatabaseHelper db;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tech_pwchange);

        //ActionbarSherlock back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        db = new DatabaseHelper(getApplicationContext());

        oldPw = (EditText) findViewById(R.id.techOldPasswordChangeEditText);
        newPw = (EditText) findViewById(R.id.techNewPasswordChangeEditText);
        repPw = (EditText) findViewById(R.id.techRepPasswordChangeEditText);

        Button confirm = (Button) findViewById(R.id.techConfirmChangeButton);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmChange();
            }
        });
    }

    private void confirmChange() {
        if (db.isCorrectTechPassword(oldPw.getText().toString())) {
            if (newPw.getText().toString().equals(repPw.getText().toString())) {
                if (!newPw.getText().toString().equals("")) {
                    db.updateTechPassword(newPw.getText().toString());
                    finish();
                } else Crouton.makeText(TechPasswordChangeActivity.this, "New password can not be empty.", Style.ALERT).show();
            } else Crouton.makeText(TechPasswordChangeActivity.this, "New passwords do not match.", Style.ALERT).show();
        } else Crouton.makeText(TechPasswordChangeActivity.this, "Wrong current password.", Style.ALERT).show();
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