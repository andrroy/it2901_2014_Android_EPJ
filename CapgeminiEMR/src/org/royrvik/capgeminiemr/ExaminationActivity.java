package org.royrvik.capgeminiemr;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.database.DatabaseHelper;

import java.util.ArrayList;


public class ExaminationActivity extends SherlockActivity {

    private ViewFlipper examinationViewFlipper;
    private TextView headerTextView, idTextView, nameTextView, imagesWithCommentTextView, imagesWithoutCommentTextView, imageHeaderTextView;
    private ImageButton returnButton, deleteButton;
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
        currentExamination.setImageUris(incomingImages);
        currentExamination.setPatientName(infoArrayList.get(1));
        currentExamination.setPatientSsn(Integer.parseInt(infoArrayList.get(0)));

        initFirstViewElements();
        initSecondViewElements();
        initExamination();
        //initTestingExamination();
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

        returnButton = (ImageButton) findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Implement
            }
        });

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
                updateEditorView();
                examinationViewFlipper.showNext();
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
                //save();
                currentImageId++;
                updateEditorView();
            }
        });

        prevButton = (Button) findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save();
                currentImageId--;
                updateEditorView();
            }
        });

        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save();
                examinationViewFlipper.showPrevious();
                updateElements();
            }
        });

        commentEditText = (EditText) findViewById(R.id.commentField);
        globalImageView = (ImageView) findViewById(R.id.imageArea);
        imageHeaderTextView = (TextView) findViewById(R.id.imageHeader);
    }

    private void initExamination() {
        String name = infoArrayList.get(0);
        String pid = infoArrayList.get(1);

        /*ArrayList<UltrasoundRowItem> images = new ArrayList<UltrasoundRowItem>();
        for (int i = 0; i < input.size(); i++) {
            images.add(new UltrasoundRowItem(input.get(i), i, ""));
        }
        examination = new Examination(name, pid, images);*/
    }

    /*private void save() {
        ArrayList<UltrasoundRowItem> temp = examination.getImages();
        temp.get(currentImageId).setDescription(commentEditText.getText().toString());
        currentExamination.updateImages(temp);
    }*/

    /**
     * For testing only
     */
    /*private void initTestingExamination() {
        ArrayList<UltrasoundRowItem> images = new ArrayList<UltrasoundRowItem>();
        images.add(new UltrasoundRowItem("", 0, ""));
        examination = new Examination("012345678910", "Frank Stangelberg", images);
    }*/

    private void updateEditorView() {
        if(currentExamination.getImageUris().size() > 0) {
            imageHeaderTextView.setText(currentImageId + 1 + " / " + currentExamination.getImageUris().size());
            globalImageView.setImageBitmap(BitmapFactory.decodeFile(currentExamination.getImageUris().get(currentImageId)));
            commentEditText.setText(currentExamination.getComments().get(currentImageId));
        }
        else {
            Crouton.makeText(this, "Something happened (updateEditorView())", Style.ALERT).show();
        }

        if (currentImageId == 0) {
            prevButton.setClickable(false);
        } else {
            prevButton.setClickable(true);
        }

        if (currentExamination.getImageUris().size() == currentImageId + 1) {
            nextButton.setClickable(false);
        } else {
            nextButton.setClickable(true);
        }
    }

    private void deleteImage() {
        ArrayList<String> temp = currentExamination.getImageUris();
        if (temp.size() > 1) {
            temp.remove(temp.get(currentImageId));
            currentExamination.setImageUris(temp);
            if (currentImageId > 0) {
                currentImageId--;
            }
            updateEditorView();
        } else {
            Crouton.makeText(this, "Something happened (deleteImage())", Style.ALERT).show();
        }
    }

    private void updateElements() {
        headerTextView.setText("Patient ID: " + Integer.toString(currentExamination.getPatientSsn()));
        idTextView.setText(Integer.toString(currentExamination.getPatientSsn()));
        nameTextView.setText(currentExamination.getPatientName());
        imagesWithCommentTextView.setText("FIX THIS image(s) with comment");
        /*int temp = examination.getImages().size() - examination.hasDesc();
        if (temp == 0) {
            imagesWithoutCommentTextView.setText("");
        } else {
            imagesWithoutCommentTextView.setText(temp + "image(s) without comment");
        }

        if (examination.getImages().size() == examination.hasDesc()) {
            reviewAndUploadButton.setClickable(true);
        }*/
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
