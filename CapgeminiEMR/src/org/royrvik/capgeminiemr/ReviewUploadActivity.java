package org.royrvik.capgeminiemr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import org.royrvik.capgeminiemr.adapter.ReviewListAdapter;
import org.royrvik.capgeminiemr.data.UltrasoundImage;
import org.royrvik.capgeminiemr.database.DatabaseHelper;
import org.royrvik.capgeminiemr.utils.SessionManager;

import java.util.List;


public class ReviewUploadActivity extends SherlockActivity {

    private DatabaseHelper dbHelper;

    private Context context;
    private SessionManager session;

    private ListView reviewListView;
    private Button editButton, uploadButton;
    private TextView reviewIdTextView, reviewNameTextView;

    private int examinationId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviewupload);

        dbHelper = new DatabaseHelper(this);

        //Getting the session
        session = new SessionManager(getApplicationContext());

        context = this;

        // get intent from last activity
        Intent i = getIntent();
        examinationId = i.getIntExtra("ex_id", 0);

        // Fetch examination from database and show its images and comments in the listview
        List<UltrasoundImage> examinationImages = dbHelper.getExamination(examinationId).getUltrasoundImages();
        reviewListView = (ListView) findViewById(R.id.reviewListView);
        reviewListView.setAdapter(new ReviewListAdapter(context, R.layout.row_list_item_review, examinationImages));

        // Buttons
        editButton = (Button) findViewById(R.id.editButton);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the current examination from the database
                Log.d("APP", "Examination ble slettet : " + Boolean.toString(dbHelper.deleteExamination(examinationId)));
                finish();
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("APP", "Do something...");
                Intent i = new Intent(ReviewUploadActivity.this, HomeScreenActivity.class);
                startActivity(i);
            }
        });

        // Textviews

        if(!session.isValid()){
            reviewIdTextView = (TextView) findViewById(R.id.reviewIdTextView);
            reviewIdTextView.setText("ID: *******");
            reviewNameTextView = (TextView) findViewById(R.id.reviewNameTextView);
            reviewNameTextView.setText("Name not available in offline mode");
        }
        else {
            reviewIdTextView = (TextView) findViewById(R.id.reviewIdTextView);
            reviewIdTextView.setText("ID: " + dbHelper.getExamination(examinationId).getPatientSsn());
            reviewNameTextView = (TextView) findViewById(R.id.reviewNameTextView);
            reviewNameTextView.setText("Name: " + dbHelper.getExamination(examinationId).getPatientName());
        }

    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }
}