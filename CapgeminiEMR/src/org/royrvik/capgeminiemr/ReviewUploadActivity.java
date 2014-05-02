package org.royrvik.capgeminiemr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.cengalabs.flatui.FlatUI;
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


public class ReviewUploadActivity extends ActionBarActivity {

    private int examinationId;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private RemoteServiceConnection service;

    private ListView reviewListView;
    private Button editButton, uploadButton;
    private TextView reviewIdTextView, reviewNameTextView;

    private List<String> data;
    private List<String> images;
    private List<String> notes;
    private Examination currentExamination;
    private Intent intent;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        FlatUI.setDefaultTheme(FlatUI.BLOOD);
        setContentView(R.layout.reviewupload);

        // Actionbar style
        FlatUI.setActionBarTheme(this, FlatUI.DARK, false, true);
        getSupportActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(FlatUI.DARK, false));
        getActionBar().setTitle(Html.fromHtml("<font color=\"#f2f2f2\">" + getResources().getString(R.string.app_name)
                + "</font>"));

        //Getting the session
        session = new SessionManager(getApplicationContext());

        dbHelper = DatabaseHelper.getInstance(this, session.getDatabaseInfo());

        //Starting connection service
        service = new RemoteServiceConnection(getApplicationContext());
        if (!service.bindService()) {
            Crouton.makeText(ReviewUploadActivity.this, "Could not connect to the EMR service", Style.ALERT);
        }

        // get intent from last activity
        Intent i = getIntent();

        currentExamination = i.getParcelableExtra("examination");

        // Fetch examination from database and show its images and comments in the listview
        final List<UltrasoundImage> examinationImages = currentExamination.getUltrasoundImages();
        reviewListView = (ListView) findViewById(R.id.reviewListView);
        reviewListView.setAdapter(new ReviewListAdapter(this, R.layout.row_list_item_review, examinationImages));

        // Buttons
        editButton = (Button) findViewById(R.id.editButton);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReviewUploadActivity.this, ExaminationActivity.class);
                i.putExtra("examination", currentExamination);
                startActivity(i);
                finish();
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check if all images have notes
                boolean validNotes = true;
                notes = currentExamination.getAllComments();
                for (String n : notes) {
                    if (n.equals(" "))
                        validNotes = false;
                }


                if (validNotes)
                    new UploadExaminationTask().execute();
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewUploadActivity.this);
                    builder.setMessage("Some images does not have notes attached. Are you sure you want to upload?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new UploadExaminationTask().execute();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }

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
            reviewIdTextView.setText("ID: " + currentExamination.getPatientSsn());
            reviewNameTextView = (TextView) findViewById(R.id.reviewNameTextView);
            reviewNameTextView.setText("Name: " + currentExamination.getPatientFirstName());
        }
    }

    private class UploadExaminationTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Working...");

            //Get examination data
            //Standard format:
            /*
            * 0 PID
            * 1 firstName
            * 2 lastName
            * 3 ExamNumber
            * 4 ExamTime
            * 5 ExamComment
            * */
            data = new ArrayList<String>();
            data.add(currentExamination.getPatientSsn());
            data.add(currentExamination.getPatientFirstName());
            data.add(currentExamination.getPatientLastName());
            data.add(currentExamination.getExaminationId().toString());
            data.add(Long.toString(currentExamination.getExaminationTime()));
            data.add(currentExamination.getExaminationComment());

            //Get images from examination
            images = currentExamination.getAllImages();

            EMRApplication settings = (EMRApplication) getApplicationContext();
            ArrayList<String> auth = settings.getDepartmentAuth();
            intent = new Intent(ReviewUploadActivity.this, HomeScreenActivity.class);

            if (service.upload(data, images, notes, auth.get(0), auth.get(1))) {
                dbHelper.deleteExamination(currentExamination.getDatabaseId());
                intent.putExtra("upload_success", "Examination successfully uploaded");
                // TODO: Delete images from device
            } else {
                intent.putExtra("upload_fail", "Upload failed");
                // TODO: append reason for failure to "fail" string
            }
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
            startActivity(intent);
            finish();
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
        updateSession();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSession();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void updateSession() {
        if (session.isValid()) {
            session.updateSession();
        }
    }

}