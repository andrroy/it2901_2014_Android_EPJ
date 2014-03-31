package org.royrvik.capgeminiemr;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import org.royrvik.capgeminiemr.adapter.ReviewListAdapter;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.data.UltrasoundImage;
import org.royrvik.capgeminiemr.database.DatabaseHelper;
import org.royrvik.capgeminiemr.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;


public class ReviewUploadActivity extends SherlockActivity {

    private DatabaseHelper dbHelper;

    private ListView reviewListView;
    private Context context;

    private SessionManager session;

    private Button editButton, uploadButton;
    private TextView reviewIdTextView, reviewNameTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviewupload);

        dbHelper = new DatabaseHelper(this);


        //Getting the session
        session = new SessionManager(getApplicationContext());

        context = this;

        // Fetch examination from database and show its images and comments in the listview
        List<UltrasoundImage> examinationImages = dbHelper.getExamination(1).getUltrasoundImages();
        reviewListView = (ListView) findViewById(R.id.reviewListView);
        reviewListView.setAdapter(new ReviewListAdapter(context, R.layout.row_list_item_review, examinationImages));

        // Buttons
        editButton = (Button) findViewById(R.id.editButton);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("APP", "Do something...");
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
            reviewIdTextView.setText("ID: " + dbHelper.getExamination(1).getPatientSsn());
            reviewNameTextView = (TextView) findViewById(R.id.reviewNameTextView);
            reviewNameTextView.setText("Name: " + dbHelper.getExamination(1).getPatientName());
        }



    }
}