package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.royrvik.capgeminiemr.data.Examination;

import java.util.ArrayList;


public class ExaminationActivity extends Activity {

    private ViewFlipper examinationViewFlipper;
    private Examination examination;
    private TextView headerTextView, idTextView, nameTextView, images1TextView, images2TextView, imageHeaderTextView;
    private ImageButton returnButton, deleteButton;
    private Button addCommentsButton, nextButton, prevButton, doneButton, reviewAndUploadButton;
    private EditText commentEditText;
    private ImageView image;
    private int currentImageId = 0;
    private UltrasoundRowItem currentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.examination);
        examinationViewFlipper = (ViewFlipper) findViewById(R.id.examinationFlipper);

        initFirstViewElements();
        initSecondViewElements();
        initExamination();
        //initTestingExamination();
        updateElements();
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
        image = (ImageView) findViewById(R.id.imageArea);
        imageHeaderTextView = (TextView) findViewById(R.id.imageHeader);
    }

    private void save() {
        ArrayList<UltrasoundRowItem> temp = examination.getImages();
        temp.get(currentImageId).setDescription(commentEditText.getText().toString());
        examination.updateImages(temp);
    }

    /**
     * For testing only
     */
    private void initTestingExamination() {
        ArrayList<UltrasoundRowItem> images = new ArrayList<UltrasoundRowItem>();
        images.add(new UltrasoundRowItem("", 0, ""));
        examination = new Examination("012345678910", "Frank Stangelberg", images);
    }

    private void initFirstViewElements() {
        headerTextView = (TextView) findViewById(R.id.header);
        idTextView = (TextView) findViewById(R.id.idField);
        nameTextView = (TextView) findViewById(R.id.nameField);
        images1TextView = (TextView) findViewById(R.id.images1);
        images2TextView = (TextView) findViewById(R.id.images2);

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

    private void updateEditorView() {
        imageHeaderTextView.setText(currentImageId + 1 + " / " + examination.getImages().size());
        currentImage = examination.getImages().get(currentImageId);
        image.setImageBitmap(BitmapFactory.decodeFile(currentImage.getImageUri()));
        commentEditText.setText(currentImage.getDescription());

        if (currentImageId == 0) {
            prevButton.setClickable(false);
        }
        else {
            prevButton.setClickable(true);
        }

        if (examination.getImages().size() == currentImageId+1) {
            nextButton.setClickable(false);
        }
        else {
            nextButton.setClickable(true);
        }
    }

    private void deleteImage() {
        ArrayList<UltrasoundRowItem> temp = examination.getImages();
        if (temp.size() > 1) {
            temp.remove(temp.get(currentImageId));
            examination.updateImages(temp);
            if (currentImageId > 0) {
                currentImageId--;
            }
            updateEditorView();
        }
        else {
            //TODO: Show error
        }
    }

    private void updateElements() {
        headerTextView.setText("Patient ID: " + examination.getPid());
        idTextView.setText(examination.getPid());
        nameTextView.setText(examination.getName());
        images1TextView.setText(examination.hasDesc() + " image(s) with comment");
        int temp = examination.getImages().size()- examination.hasDesc();
        if (temp == 0) {
            images2TextView.setText("");
        }
        else {
            images2TextView.setText(temp + " image(s) without comment");
        }

        if (examination.getImages().size() == examination.hasDesc()) {
            reviewAndUploadButton.setClickable(true);
        }
    }

    private void initExamination() {
        Intent intent = getIntent();
        ArrayList<String> input = intent.getStringArrayListExtra("info");
        String name = input.remove(0);
        String pid = input.remove(0);

        ArrayList<UltrasoundRowItem> images = new ArrayList<UltrasoundRowItem>();
        for (int i=0;i<input.size();i++) {
            images.add(new UltrasoundRowItem(input.get(i), i, ""));
        }
        examination = new Examination(name, pid, images);
    }
}
