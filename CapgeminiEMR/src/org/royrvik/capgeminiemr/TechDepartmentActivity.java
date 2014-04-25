package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.royrvik.capgeminiemr.database.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by Joakim on 25.04.2014.
 */
public class TechDepartmentActivity extends Activity {

    private EditText usernameEditText, passwordEditText;
    private Button confirmButton;
    private DatabaseHelper db;
    private ArrayList<String> savedData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tech_department);

        db = new DatabaseHelper(getApplicationContext());
        usernameEditText = (EditText) findViewById(R.id.departmentUnameEditText);
        passwordEditText = (EditText) findViewById(R.id.departmentPwordEditText);
        confirmButton = (Button) findViewById(R.id.departmentConfirmButton);

        savedData = db.getDepartmentAuth();
        if (savedData.size() > 1) {
            usernameEditText.setText(savedData.get(0));
            passwordEditText.setText(savedData.get(1));
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.setDepartmentAuth(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                finish();
            }
        });
    }
}