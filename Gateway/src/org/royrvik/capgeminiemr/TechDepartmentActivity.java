package org.royrvik.capgeminiemr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.cengalabs.flatui.FlatUI;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.ArrayList;

public class TechDepartmentActivity extends ActionBarActivity {

    private EditText usernameEditText, passwordEditText;

    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        FlatUI.setDefaultTheme(FlatUI.BLOOD);
        setContentView(R.layout.tech_department);

        // Actionbar style
        FlatUI.setActionBarTheme(this, FlatUI.DARK, false, true);
        getSupportActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(FlatUI.DARK, false));
        getActionBar().setTitle(Html.fromHtml("<font color=\"#f2f2f2\"> <em><b>" + getResources().getString(R.string.app_name)
                + "</b></em></font>"));

        //Actionbar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backbtn);

        usernameEditText = (EditText) findViewById(R.id.departmentUnameEditText);
        passwordEditText = (EditText) findViewById(R.id.departmentPwordEditText);
        Button confirmButton = (Button) findViewById(R.id.departmentConfirmButton);

        final EMRApplication settings = (EMRApplication) getApplicationContext();
        ArrayList<String> savedData = settings.getDepartmentAuth();
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
                    settings.setDepartmentAuth(usernameEditText.getText().toString(), passwordEditText.getText().toString());
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