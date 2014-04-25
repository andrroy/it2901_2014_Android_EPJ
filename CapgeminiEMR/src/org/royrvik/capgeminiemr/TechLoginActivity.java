package org.royrvik.capgeminiemr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.database.DatabaseHelper;

/**
 * Created by Joakim.
 */
public class TechLoginActivity extends SherlockActivity {
    private TextView techLoginTextView;
    private TextView techLoginConfirmTextView;
    private EditText techLoginPasswordEditText;
    private EditText techLoginConfirmPasswordEditText;
    private DatabaseHelper dbHelper;
    private Intent launchIntent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tech_login);

        //ActionbarSherlock back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        dbHelper = new DatabaseHelper(getApplicationContext());

        techLoginTextView = (TextView) findViewById(R.id.techLoginTextView);
        techLoginConfirmTextView = (TextView) findViewById(R.id.techLoginConfirmTextView);
        techLoginPasswordEditText = (EditText) findViewById(R.id.techLoginPasswordEditText);
        techLoginConfirmPasswordEditText = (EditText) findViewById(R.id.techLoginConfirmPasswordEditText);
        Button loginButton = (Button) findViewById(R.id.techLoginOkButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkTechPassword();
            }
        });
        int type = getIntent().getIntExtra("type", 0);
        switch (type) {
            case 1:
                launchIntent = new Intent(TechLoginActivity.this, TechnicalSetupActivity.class);
                break;
            case 2:
                launchIntent = new Intent(TechLoginActivity.this, CurrentSetupActivity.class);
                break;
            case 3:
                launchIntent = new Intent(TechLoginActivity.this, TechDepartmentActivity.class);
                break;
            default:
                System.out.println("Unsupported launch command.");
                System.out.println(getIntent().getIntExtra("type", 0));
                finish();
        }
        updateLoginView();
    }

    /**
     *
     */
    private void checkTechPassword() {
        if (isFirstSetup()) {
            if (techLoginPasswordEditText.getText().toString().equals(techLoginConfirmPasswordEditText.getText().toString())) {
                if (dbHelper.saveTechPassword(techLoginPasswordEditText.getText().toString())) {
                    startActivity(launchIntent);
                    finish();
                    return;
                }
                else {
                    Crouton.makeText(TechLoginActivity.this, "Something went wrong: A tech user password is already saved to th database.", Style.ALERT).show();
                }
            }
            else {
                Crouton.makeText(TechLoginActivity.this, "The passwords does not match!", Style.ALERT).show();
            }
        }
        if (dbHelper.isCorrectTechPassword(techLoginPasswordEditText.getText().toString())) {
            startActivity(launchIntent);
            finish();
        }
    }

    /**
     * Updates the login view
     */
    private void updateLoginView() {
        if (isFirstSetup()) {
            techLoginTextView.setText("Please choose a password for technical users");
            techLoginConfirmPasswordEditText.setEnabled(true);
            techLoginConfirmTextView.setText("Confirm new password");
        }
        else {
            techLoginTextView.setText("Please enter technical user password");
            techLoginConfirmPasswordEditText.setEnabled(false);
            techLoginConfirmPasswordEditText.setVisibility(View.GONE);
            techLoginConfirmTextView.setText("");
        }
    }

    /**
     *
     * @return True if the tech password is not set.
     */
    private boolean isFirstSetup() {
        return !dbHelper.isTechPasswordSet();
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