package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.royrvik.capgeminiemr.adapter.Examination;
import org.royrvik.capgeminiemr.adapter.UltrasoundRowItem;

import java.util.ArrayList;

/**
 * Created by Joakim on 27.02.14.
 */
public class ExaminationView extends Activity {

    private ViewFlipper flipper;
    private Examination data;
    private TextView header, idField, nameField, images1, images2, imageHeader;
    private ImageButton returnButton, deleteButton;
    private Button addCommentsButton, nextButton, prevButton, doneButton, reviewAndUploadButton;
    private EditText commentField;
    private ImageView image;
    private int currentImageId = 0;
    private UltrasoundRowItem currentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.examination);
        flipper = (ViewFlipper) findViewById(R.id.examinationFlipper);

        initFirstViewElements();
        initSecondViewElements();
        //initExamination();
        initTestingExamination();
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
            }
        });

        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                flipper.showPrevious();
                updateElements();
            }
        });

        commentField = (EditText) findViewById(R.id.commentField);
        image = (ImageView) findViewById(R.id.imageArea);
        imageHeader = (TextView) findViewById(R.id.imageHeader);
    }

    private void save() {
        ArrayList<UltrasoundRowItem> temp = data.getImages();
        temp.get(currentImageId).setDescription(commentField.getText().toString());
        data.updateImages(temp);
    }

    /**
     * For testing only
     */
    private void initTestingExamination() {
        ArrayList<UltrasoundRowItem> images = new ArrayList<UltrasoundRowItem>();
        images.add(new UltrasoundRowItem("", 0, ""));
        data = new Examination("012345678910", "Frank Stangelberg", images);
    }

    private void initFirstViewElements() {
        header = (TextView) findViewById(R.id.header);
        idField = (TextView) findViewById(R.id.idField);
        nameField = (TextView) findViewById(R.id.nameField);
        images1 = (TextView) findViewById(R.id.images1);
        images2 = (TextView) findViewById(R.id.images2);

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
            }
        });
        reviewAndUploadButton.setClickable(false);

        addCommentsButton = (Button) findViewById(R.id.addCommentsButton);
        addCommentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEditorView();
                flipper.showNext();
            }
        });
    }

    private void updateEditorView() {
        imageHeader.setText(currentImageId +1 + " / " + data.getImages().size());

        currentImage = data.getImages().get(currentImageId);
        image.setImageBitmap(BitmapFactory.decodeFile(currentImage.getImageUri()));
        commentField.setText(currentImage.getDescription());

        if (currentImageId == 0) {
            prevButton.setClickable(false);
        }
        else {
            prevButton.setClickable(true);
        }

        if (currentImageId+1 < data.getImages().size()) {
            nextButton.setClickable(false);
        }
        else {
            nextButton.setClickable(true);
        }
    }

    private void deleteImage() {
        ArrayList<UltrasoundRowItem> temp = data.getImages();
        temp.remove(temp.get(currentImageId));
        data.updateImages(temp);
        if (currentImageId > 0) {
            currentImageId--;
        }
        updateEditorView();
    }

    private void updateElements() {
        header.setText("Patient ID: "+data.getPid());
        idField.setText(data.getPid());
        nameField.setText(data.getName());
        images1.setText(data.hasDesc() + " image(s) with comment");
        int temp = data.getImages().size()-data.hasDesc();
        if (temp == 0) {
            images2.setText("");
        }
        else {
            images2.setText(temp + " image(s) without comment");
        }

        if (data.getImages().size() == data.hasDesc()) {
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
        data = new Examination(name, pid, images);
    }
}
