package org.royrvik.capgeminiemr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.database.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by Joakim.
 */
public class TechDepartmentActivity extends SherlockActivity {

    private EditText usernameEditText, passwordEditText;
    private DatabaseHelper db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tech_department);

        //ActionbarSherlock back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        db = new DatabaseHelper(getApplicationContext());
        usernameEditText = (EditText) findViewById(R.id.departmentUnameEditText);
        passwordEditText = (EditText) findViewById(R.id.departmentPwordEditText);
        Button confirmButton = (Button) findViewById(R.id.departmentConfirmButton);

        ArrayList<String> savedData = db.getDepartmentAuth();
        if (savedData.size() > 1) {
            usernameEditText.setText(savedData.get(0));
            passwordEditText.setText(savedData.get(1));
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password;
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                if (!username.equals("") && !password.equals("")) {
                    db.setDepartmentAuth(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                    finish();
                } else
                    Crouton.makeText(TechDepartmentActivity.this, "One or more fields are empty.", Style.ALERT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back button clicked.
                if (getIntent().getBooleanExtra("init", false)) {
                    startActivity(new Intent(TechDepartmentActivity.this, TechnicalSetupActivity.class));
                }
                // Exit activity and open previous in activity stack
                finish();
                break;
        }
        return true;
    }
}