package org.royrvik.capgeminiemr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
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
import org.royrvik.capgeminiemr.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class ReviewUploadActivity extends ActionBarActivity {

    private int examinationId;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private RemoteServiceConnection service;

    private ListView reviewListView;
    private Button editButton, uploadButton;
    private TextView reviewExamNumberTextView, reviewFirstNameTextView, reviewLastNameTextView, examCommentTextView,
        reviewPatientIDTextView, reviewDateOfBirthTextView, reviewExamDateTextView;
    private List<String> data;
    private List<String> images;
    private List<String> notes;
    private Examination currentExamination;
    private Intent intent;
    private ProgressDialog pDialog;
    private View headerView, footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        FlatUI.setDefaultTheme(FlatUI.BLOOD);
        setContentView(R.layout.reviewupload);

        // Actionbar style
        FlatUI.setActionBarTheme(this, FlatUI.DARK, false, true);
        getSupportActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(FlatUI.DARK, false));
        getActionBar().setTitle(Html.fromHtml("<font color=\"#f2f2f2\"> <em><b>" + getResources().getString(R.string.app_name)
                + "</b></em></font>"));

        // Getting the session
        session = new SessionManager(getApplicationContext());

        dbHelper = DatabaseHelper.getInstance(this, session.getDatabaseInfo());

        // Starting connection service
        service = new RemoteServiceConnection(getApplicationContext());
        if (!service.bindService()) {
            Crouton.makeText(ReviewUploadActivity.this, "Could not connect to the EMR service", Style.ALERT);
        }

        // Get intent from last activity
        Intent i = getIntent();

        currentExamination = i.getParcelableExtra("examination");
        Log.d("APP:", "Current examination: " + currentExamination.toString());

        // Fetch examination from database and show its images and comments in the listview
        final List<UltrasoundImage> examinationImages = currentExamination.getUltrasoundImages();

        reviewListView = (ListView) findViewById(R.id.reviewListView);

        // Header init
        LayoutInflater inflater = LayoutInflater.from(this);
        headerView = inflater.inflate(R.layout.review_n_upload_header, null);
        reviewExamNumberTextView = (TextView) headerView.findViewById(R.id.reviewExamNumberTextView);
        reviewFirstNameTextView = (TextView) headerView.findViewById(R.id.reviewPatientFirstNameTextView);
        reviewLastNameTextView = (TextView) headerView.findViewById(R.id.reviewPatientLastNameTextView);
        examCommentTextView = (TextView) headerView.findViewById(R.id.reviewCommentTextView);
        reviewPatientIDTextView = (TextView) headerView.findViewById(R.id.reviewSSNtextView);
        reviewDateOfBirthTextView = (TextView) headerView.findViewById(R.id.reviewPatientDobTextView);
        reviewExamDateTextView = (TextView) headerView.findViewById(R.id.reviewExamDateTextView);

        // Set text in header textviews
        reviewExamNumberTextView.setText(getResources().getString(R.string.exam) + " " + currentExamination.getExaminationNumber());

        // Check if examination has a comment
        if(currentExamination.getExaminationComment() == null) {
            examCommentTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.exam_comment) + "</b> " +
                    ""));
        }
        else {
            examCommentTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.exam_comment) + "</b> " +
                    currentExamination.getExaminationComment()));
        }

        reviewDateOfBirthTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.date_of_birth) + "</b> " +
                Utils.ssnToDateOfBirth(currentExamination.getPatientSsn())));
        reviewExamDateTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.conducted) + "</b> " +
                Utils.formattedDate(currentExamination.getExaminationTime())));



        // Footer
        footerView = inflater.inflate(R.layout.review_n_upload_footer, null);

        reviewListView.addFooterView(footerView);
        reviewListView.addHeaderView(headerView);
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
     *
     */
    private void updateTextViews() {
        if (!session.isValid()) {
            reviewPatientIDTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.patient_id) + "</b> " +
                    "not available in offline mode"));
            reviewFirstNameTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.first_name) + "</b> " +
                    "not available in offline mode"));
            reviewLastNameTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.last_name) + "</b> " +
                    "not available in offline mode"));
        } else {
            reviewPatientIDTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.patient_id) + "</b> " +
                    currentExamination.getPatientSsn()));
            reviewFirstNameTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.first_name) + "</b> " +
                    currentExamination.getPatientFirstName()));
            reviewLastNameTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.last_name) + "</b> " +
                    currentExamination.getPatientLastName()));
        }
    }

    private class UploadExaminationTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Uploading examination...");

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
            data.add(currentExamination.getExaminationNumber().toString());
            data.add(Long.toString(currentExamination.getExaminationTime()));
            data.add(currentExamination.getExaminationComment());

            //Get images from examination
            images = currentExamination.getAllImages();

            EMRApplication settings = (EMRApplication) getApplicationContext();
            ArrayList<String> auth = settings.getDepartmentAuth();
            intent = new Intent(ReviewUploadActivity.this, HomeScreenActivity.class);

            List<String> uploadResponse = null;
            uploadResponse = service.upload(data, images, notes, auth.get(0), auth.get(1));
            if (uploadResponse==null || !Boolean.valueOf(uploadResponse.get(0))) {
                intent.putExtra("upload_fail", "Upload failed");
                // TODO: append reason for failure to "fail" string
            } else {
                dbHelper.deleteExamination(currentExamination.getId());
                intent.putExtra("upload_success", "Examination successfully uploaded");
                // TODO: Delete images from device
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ReviewUploadActivity.this);
            pDialog.setMessage("Uploading examination...");
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

    @Override
    public void onBackPressed(){
        dbHelper.updateExamination(currentExamination);
        Intent i = new Intent(this, ExaminationActivity.class);
        i.putExtra("examination", currentExamination);
        startActivity(i);
        finish();
    }

    private void updateSession() {
        if (session.isValid()) {
            session.updateSession();
        }
    }
}