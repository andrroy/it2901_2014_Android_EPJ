package org.royrvik.capgeminiemr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
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

    //Temp
    private List<String> data;
    private List<String> images;
    private List<String> notes;
    private Examination ex;

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
        ex = dbHelper.getExamination(examinationId);

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

                //Check if all images have notes
                boolean validNotes = true;
                notes = ex.getAllComments();
                for(String n : notes){
                    if(n.equals(" ")) validNotes=false;
                }
                if(validNotes)new UploadExaminationTask().execute();
                else Toast.makeText(getApplicationContext(), "Some images does not have notes attached", Toast.LENGTH_SHORT).show();

            }
        });

        updateTextViews();
    }

    /**
     * Updates the TextViews with sensitive information, based on the status of the current session.
     */
    private void updateTextViews() {
        if (!session.isValid()) {
            reviewIdTextView = (TextView) findViewById(R.id.reviewIdTextView);
            reviewIdTextView.setText("ID: *******");
            reviewNameTextView = (TextView) findViewById(R.id.reviewNameTextView);
            reviewNameTextView.setText("Name: not available in offline mode");
        } else {
            reviewIdTextView = (TextView) findViewById(R.id.reviewIdTextView);
            reviewIdTextView.setText("ID: " + dbHelper.getExamination(examinationId).getPatientSsn());
            reviewNameTextView = (TextView) findViewById(R.id.reviewNameTextView);
            reviewNameTextView.setText("Name: " + dbHelper.getExamination(examinationId).getPatientName());
        }
    }

    private class UploadExaminationTask extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Working...");

            //Get patient data
            data = new ArrayList<String>();
            data.add(ex.getPatientSsn());
            data.add(ex.getPatientName());
            //Get images from examination
            images = ex.getAllImages();

            // TODO: Get credentials from database
            String username = "rikardbe_emr";
            String password = "Paa5Eric";

            Intent i = new Intent(ReviewUploadActivity.this, HomeScreenActivity.class);

            if (service.upload(data, images, notes, username, password)) {
                dbHelper.deleteExamination(examinationId);
                i.putExtra("upload_success", "Examination successfully uploaded");
                // TODO: Delete images from device
            } else {
                i.putExtra("upload_fail", "Upload failed");
                // TODO: append reason for failure to "fail" string
            }

            startActivity(i);
            finish();
            return null;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ReviewUploadActivity.this);
            pDialog.setMessage("Working...");
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
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