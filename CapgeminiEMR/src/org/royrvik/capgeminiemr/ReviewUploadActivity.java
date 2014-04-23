package org.royrvik.capgeminiemr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.adapter.ReviewListAdapter;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.data.UltrasoundImage;
import org.royrvik.capgeminiemr.database.DatabaseHelper;
import org.royrvik.capgeminiemr.utils.RemoteServiceConnection;
import org.royrvik.capgeminiemr.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;


public class ReviewUploadActivity extends SherlockActivity {

    private int examinationId;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private RemoteServiceConnection service;

    private ListView reviewListView;
    private Button editButton, uploadButton;
    private TextView reviewIdTextView, reviewNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviewupload);

        dbHelper = new DatabaseHelper(this);

        //Getting the session
        session = new SessionManager(getApplicationContext());

        //Starting connection service
        service = new RemoteServiceConnection(getApplicationContext());
        if (!service.bindService()) {
            Crouton.makeText(ReviewUploadActivity.this, "Could not connect to the EMR service", Style.ALERT);
        }

        // get intent from last activity
        Intent i = getIntent();
        examinationId = i.getIntExtra("ex_id", 0);

        // Fetch examination from database and show its images and comments in the listview
        final List<UltrasoundImage> examinationImages = dbHelper.getExamination(examinationId).getUltrasoundImages();
        reviewListView = (ListView) findViewById(R.id.reviewListView);
        reviewListView.setAdapter(new ReviewListAdapter(this, R.layout.row_list_item_review, examinationImages));

        // Buttons
        editButton = (Button) findViewById(R.id.editButton);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReviewUploadActivity.this, ExaminationActivity.class);
                i.putExtra("ex_id", examinationId);
                startActivity(i);
                finish();
            }
        });


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> data = new ArrayList<String>();
                Examination ex = dbHelper.getExamination(examinationId);
                data.add(ex.getPatientSsn());
                data.add(ex.getPatientName());
                List<String> images = ex.getAllImages();
                if (service.upload(data, images)) {
                    dbHelper.deleteExamination(examinationId);
                }
                else Crouton.makeText(ReviewUploadActivity.this, "Upload Failed", Style.ALERT);

                //TODO: At this point, the application should save the data

                Intent i = new Intent(ReviewUploadActivity.this, HomeScreenActivity.class);
                startActivity(i);
                finish();
            }
        });

        updateTextViews();
    }

    /**
     * Updates the TextViews with sensitive information, based on the status of the current session.
     */
    private void updateTextViews() {
        if(!session.isValid()){
            reviewIdTextView = (TextView) findViewById(R.id.reviewIdTextView);
            reviewIdTextView.setText("ID: *******");
            reviewNameTextView = (TextView) findViewById(R.id.reviewNameTextView);
            reviewNameTextView.setText("Name: not available in offline mode");
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
        service.releaseService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTextViews();
    }

}