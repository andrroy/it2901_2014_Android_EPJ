package org.royrvik.capgeminiemr;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.data.UltrasoundImage;
import org.royrvik.capgeminiemr.database.DatabaseHelper;
import org.royrvik.capgeminiemr.utils.SessionManager;

import java.util.ArrayList;


public class ExaminationActivity extends SherlockActivity {

    private ViewFlipper examinationViewFlipper;
    private TextView headerTextView, idTextView, nameTextView, imagesWithCommentTextView, imagesWithoutCommentTextView, imageHeaderTextView;
    private ImageButton deleteButton;
    private Button addCommentsButton, nextButton, prevButton, doneButton, reviewAndUploadButton;
    private EditText commentEditText;
    private ImageView globalImageView;
    private int currentImageId = 0;
    private Examination currentExamination;
    private ArrayList<String> incomingImages;
    private ArrayList<String> infoArrayList = new ArrayList<String>();
    private DatabaseHelper dbHelper;
    private SessionManager session;

    // For logging
    private static final String TAG = "APP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.examination);
        examinationViewFlipper = (ViewFlipper) findViewById(R.id.examinationFlipper);

        dbHelper = new DatabaseHelper(this);

        //Getting the session
        session = new SessionManager(getApplicationContext());


        // get intent from last activity
        Intent intent = getIntent();
        // Check what kind of extras intent has, and create/fetch examination based on result
        if(intent.hasExtra("chosen_images") && intent.hasExtra("info")) {
            // Activity started from IdentifyPatientActivity
            incomingImages = intent.getStringArrayListExtra("chosen_images");
            infoArrayList = intent.getStringArrayListExtra("info");
            currentExamination = new Examination();
            currentExamination.setPatientName(infoArrayList.get(1));
            currentExamination.setPatientSsn(infoArrayList.get(0));
            for (String uri : incomingImages) {
                currentExamination.addUltrasoundImage(new UltrasoundImage(uri));
            }
        }
        else if(intent.hasExtra("ex_id")) {
            // Activity started from Homescreen
            int exId = intent.getIntExtra("ex_id", 0);
            currentExamination = dbHelper.getExamination(exId);

        }


        // Initialize GUI elements
        initFirstViewElements();
        initSecondViewElements();
        updateElements();

        //ActionbarSherlock back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void initFirstViewElements() {
        headerTextView = (TextView) findViewById(R.id.header);
        idTextView = (TextView) findViewById(R.id.idField);
        nameTextView = (TextView) findViewById(R.id.nameField);
        imagesWithCommentTextView = (TextView) findViewById(R.id.images1);
        imagesWithoutCommentTextView = (TextView) findViewById(R.id.images2);

        reviewAndUploadButton = (Button) findViewById(R.id.reviewAndUploadButton);
        reviewAndUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add examination to database and retrieve its examination_id
                int exId = dbHelper.addExamination(currentExamination);
                currentExamination.setId(exId);
                // Start ReviewUpload and attach the examination id
                Intent i = new Intent(ExaminationActivity.this, ReviewUploadActivity.class);
                i.putExtra("ex_id", exId);
                startActivity(i);
            }
        });

        addCommentsButton = (Button) findViewById(R.id.addCommentsButton);
        addCommentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentExamination.getUltrasoundImages().isEmpty()) {
                    Crouton.makeText(ExaminationActivity.this, "You don't have any images to add comments to (this is not supposed to happen)", Style.ALERT).show();
                } else {
                    examinationViewFlipper.showNext();
                }
                updateEditorView();

            }
        });
    }

    private void initSecondViewElements() {
        deleteButton = (ImageButton) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImage();
            }
        });

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                currentImageId++;
                updateEditorView();
            }
        });

        prevButton = (Button) findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                currentImageId--;
                updateEditorView();
            }
        });

        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                examinationViewFlipper.showPrevious();
                updateElements();
            }
        });

        commentEditText = (EditText) findViewById(R.id.commentField);
        globalImageView = (ImageView) findViewById(R.id.imageArea);
        imageHeaderTextView = (TextView) findViewById(R.id.imageHeader);
    }

    private void save() {

        // Sets the comment to the current UltrasoundImage to the text in commentEditText
        currentExamination.getUltrasoundImages().get(currentImageId).setComment(commentEditText.getText().toString());

    }

    private void updateEditorView() {
        if (currentExamination.getUltrasoundImages().size() > 0) {
            imageHeaderTextView.setText(currentImageId + 1 + " / " + currentExamination.getUltrasoundImages().size());
            globalImageView.setImageBitmap(BitmapFactory.decodeFile(currentExamination.getUltrasoundImages().get(currentImageId).getImageUri()));
            commentEditText.setText(currentExamination.getUltrasoundImages().get(currentImageId).getComment());
        }

        if (currentImageId == 0) {
            prevButton.setClickable(false);
        } else {
            prevButton.setClickable(true);
        }

        if (currentExamination.getUltrasoundImages().size() == currentImageId + 1) {
            nextButton.setClickable(false);
        } else {
            nextButton.setClickable(true);
        }
    }

    private void deleteImage() {

        if (currentExamination.getUltrasoundImages().size() <= 1) {
            Crouton.makeText(this, "You can't delete your only image!", Style.ALERT).show();
        } else {
            currentExamination.deleteImage(currentImageId);
            if (currentImageId > 0)
                currentImageId--;
            Crouton.makeText(this, "Image deleted", Style.CONFIRM).show();
            updateEditorView();
        }

    }

    private void updateElements() {
        if(!session.isValid()){
            headerTextView.setText("Patient ID: *******");
            nameTextView.setText("Name not available in offline mode");
            idTextView.setText("*******");
        }
        else {
            headerTextView.setText("Patient ID: " + currentExamination.getPatientSsn());
            idTextView.setText(currentExamination.getPatientSsn());
            nameTextView.setText(currentExamination.getPatientName());
        }

        int imagesWithComment = 0;
        int imagesWithoutComment = 0;
        for (UltrasoundImage usi : currentExamination.getUltrasoundImages()) {
            if (usi.getComment() == " ")
                imagesWithoutComment++;
            else
                imagesWithComment++;
        }

        imagesWithoutCommentTextView.setText(imagesWithoutComment + " image(s) without comment");
        imagesWithCommentTextView.setText(imagesWithComment + " image(s) with comment");

        // Reset font color
        imagesWithoutCommentTextView.setTextColor(Color.BLACK);

        if (imagesWithoutComment > 0)
            imagesWithoutCommentTextView.setTextColor(Color.RED);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back button clicked. Exit activity and open previous in activity stack
                dbHelper.addExamination(currentExamination);
                startActivity(new Intent(this, HomeScreenActivity.class));
                finish();
                break;
        }
        return true;
    }
}
