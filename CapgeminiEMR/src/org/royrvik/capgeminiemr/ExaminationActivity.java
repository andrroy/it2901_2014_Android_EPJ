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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.examination);
        examinationViewFlipper = (ViewFlipper) findViewById(R.id.examinationFlipper);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // get intent from last activity
        Intent i = getIntent();
        incomingImages = i.getStringArrayListExtra("chosen_images");
        infoArrayList = i.getStringArrayListExtra("info");

        currentExamination = new Examination();
        currentExamination.setPatientName(infoArrayList.get(1));
        currentExamination.setPatientSsn(Integer.parseInt(infoArrayList.get(0)));
        for (String uri : incomingImages) {
            currentExamination.addUltrasoundImage(new UltrasoundImage(uri));
        }

        initFirstViewElements();
        initSecondViewElements();
        updateElements();

        //Actionbarsherlock back button
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
                //TODO: Implement
                finish();
            }
        });
        reviewAndUploadButton.setClickable(false);

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
        headerTextView.setText("Patient ID: " + Integer.toString(currentExamination.getPatientSsn()));
        idTextView.setText(Integer.toString(currentExamination.getPatientSsn()));
        nameTextView.setText(currentExamination.getPatientName());

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

        if (imagesWithoutComment > 0)
            imagesWithoutCommentTextView.setTextColor(Color.RED);

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
