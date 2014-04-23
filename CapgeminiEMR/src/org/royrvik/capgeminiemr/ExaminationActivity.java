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
import net.sqlcipher.database.SQLiteDatabase;
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
    private DatabaseHelper dbHelper;
    private SessionManager session;

    // For logging
    private static final String TAG = "APP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.examination);
        examinationViewFlipper = (ViewFlipper) findViewById(R.id.examinationFlipper);

        SQLiteDatabase.loadLibs(getApplicationContext());
        dbHelper = new DatabaseHelper(this);

        //Getting the session
        session = new SessionManager(getApplicationContext());

        // Check where the activity was launched from and choose appropriate action based on result
        Intent intent = getIntent();
        if(activityStartedFrom().equals("IdentifyPatientActivity")) {
            ArrayList<String> incomingImages = intent.getStringArrayListExtra("chosen_images");
            ArrayList<String> infoArrayList = intent.getStringArrayListExtra("info");
            currentExamination = new Examination();
            if (infoArrayList.size() < 2) {
                currentExamination.setPatientName("No name received from intent.");
            }
            else {
                currentExamination.setPatientName(infoArrayList.get(1));
            }
            currentExamination.setPatientSsn(infoArrayList.get(0));
            for (String uri : incomingImages) {
                currentExamination.addUltrasoundImage(new UltrasoundImage(uri));
            }
        }
        else if(activityStartedFrom().equals("HomeScreenActivity")) {
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
                // If activity started from HomeScreen, the examination should already
                // exist in the database. In which case we should delete it first
                if(activityStartedFrom().equals("HomeScreenActivity"))
                    dbHelper.deleteExamination(currentExamination.getId());

                // Add examination to database and retrieve its examination_id
                int exId = dbHelper.addExamination(currentExamination);
                currentExamination.setId(exId);

                // Start ReviewUpload and add the examination id as an extra in the intent
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

        //Disable prevButton if current image is the first.
        if (currentImageId == 0) prevButton.setClickable(false);
        else prevButton.setClickable(true);

        //Disable nextButton if current image is the last.
        if (currentExamination.getUltrasoundImages().size() == currentImageId + 1) nextButton.setClickable(false);
        else nextButton.setClickable(true);
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
            nameTextView.setText("Name: not available in offline mode");
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

    /**
     * Returns the name of the activity which started this activity
     */
    private String activityStartedFrom() {
        // get intent from last activity
        Intent intent = getIntent();
        // Check what kind of extras intent has
        if(intent.hasExtra("chosen_images") && intent.hasExtra("info")) {
            // Activity started from IdentifyPatientActivity
            return "IdentifyPatientActivity";
        }
        else if(intent.hasExtra("ex_id")) {
            // Activity started from HomeScreenActivity
            return "HomeScreenActivity";
        }
        return null;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back button clicked. Exit activity and open previous in activity stack
                startActivity(new Intent(this, HomeScreenActivity.class));
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateElements();
    }
}
