package org.royrvik.capgeminiemr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.cengalabs.flatui.FlatUI;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.data.UltrasoundImage;
import org.royrvik.capgeminiemr.database.DatabaseHelper;
import org.royrvik.capgeminiemr.utils.SessionManager;

import java.util.ArrayList;


public class ExaminationActivity extends ActionBarActivity {

    private static final int REQUEST_CODE = 5;
    private static final int FULLSCREEN_REQUEST_CODE = 15;
    private TextView idTextView, firstNameTextView, lastNameTextView,
            imagesWithoutCommentTextView, dateOfBirthTextView;
    //private ImageButton greenidStatusImageButton;
    private ImageButton editIDImageButton;
    private ImageView isVerifiedImageView;
    private Button viewImagesButton, reviewAndUploadButton;
    //private EditText examinationCommentEditText;
    private Examination currentExamination;
    private DatabaseHelper dbHelper;
    private SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        FlatUI.setDefaultTheme(FlatUI.BLOOD);
        setContentView(R.layout.examination);

        // Actionbar style
        FlatUI.setActionBarTheme(this, FlatUI.DARK, false, true);
        getSupportActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(FlatUI.DARK, false));
        getActionBar().setTitle(Html.fromHtml("<font color=\"#f2f2f2\">" + getResources().getString(R.string.app_name)
                + "</font>"));

        //Actionbar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        dbHelper = DatabaseHelper.getInstance(this);

        //Getting the session
        session = new SessionManager(getApplicationContext());

        // Check where the activity was launched from and choose appropriate action based on result
        Intent intent = getIntent();
        // Activity was started to create a new examination
        if (activityStartedForAction().equals("new_examination")) {
            ArrayList<String> incomingImages = intent.getStringArrayListExtra("chosen_images");
            ArrayList<String> infoArrayList = intent.getStringArrayListExtra("info");
            currentExamination = new Examination();
            if (infoArrayList.size() < 2) {
                currentExamination.setPatientName("");
            } else {
                currentExamination.setPatientName(infoArrayList.get(1));
            }
            currentExamination.setPatientSsn(infoArrayList.get(0));
            for (String uri : incomingImages) {
                currentExamination.addUltrasoundImage(new UltrasoundImage(uri));
            }
        }

        // Activity was started to edit an existing examination
        else if (activityStartedForAction().equals("edit_examination")) {
            int exId = intent.getIntExtra("ex_id", -1);
            if (exId != -1) {
                currentExamination = dbHelper.getExamination(exId);
            } else finish();
        } else if (activityStartedForAction().equals("edit_examinationObject")) {
            currentExamination = intent.getParcelableExtra("examination");
        }

        // Initialize GUI elements
        initFirstViewElements();
        updateElements();

    }

    private void initFirstViewElements() {
        idTextView = (TextView) findViewById(R.id.examPatientIDTextView);
        firstNameTextView = (TextView) findViewById(R.id.examPatientFirstNameTextView);
        lastNameTextView = (TextView) findViewById(R.id.examPatientLastNameTextView);
        imagesWithoutCommentTextView = (TextView) findViewById(R.id.imagesWithoutCommentTextView);
        editIDImageButton = (ImageButton) findViewById(R.id.editIDImageButton);
        dateOfBirthTextView = (TextView) findViewById(R.id.examDateTextView);
        isVerifiedImageView = (ImageView) findViewById(R.id.isVerifiedImageView);

        editIDImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ExaminationActivity.this, IdentifyPatientActivity.class);
                i.putExtra("id", currentExamination.getPatientSsn());
                i.putExtra("return", true);
                startActivityForResult(i, REQUEST_CODE);
            }
        });

        /*greenidStatusImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ExaminationActivity.this, IdentifyPatientActivity.class);
                i.putExtra("id", currentExamination.getPatientSsn());
                i.putExtra("return", true);
                startActivityForResult(i, REQUEST_CODE);
            }
        });*/


        //Updates the verification imageview.
        isVerifiedImageView.setImageResource(R.drawable.ic_navigation_cancel);
        if (idIsValidated()) {
            Log.d("APP", "isValidated");
            isVerifiedImageView.setImageResource(R.drawable.ic_navigation_accept);
        }

        reviewAndUploadButton = (Button) findViewById(R.id.reviewUploadButton);
        reviewAndUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (!idIsValidated()) return;

                // Choose action based on why this activity was started
                int exId;
                Log.d("APP:", "Current exID: " + currentExamination.getId());
                if (currentExamination.getId() == -1) {
                    exId = dbHelper.addExamination(currentExamination);
                    Log.d("APP:", "saved new examination");
                    currentExamination.setId(exId);
                } else {
                    dbHelper.updateExamination(currentExamination);
                    Log.d("APP:", "Updated existing examination");
                    exId = currentExamination.getId();
                }

                // Start ReviewUpload and add the examination id as an extra in the intent
                Intent i = new Intent(ExaminationActivity.this, ReviewUploadActivity.class);
                i.putExtra("examination", currentExamination);
                startActivity(i);
                //finish();
            }
        });

        viewImagesButton = (Button) findViewById(R.id.examViewImagesButton);
        viewImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentExamination.getUltrasoundImages().isEmpty()) {
                    Crouton.makeText(ExaminationActivity.this,
                            "You don't have any images to add comments to (this is not supposed to happen)",
                            Style.ALERT).show();
                } else {
                    // Start FullScreenViewActivity here. - Rix1
                    Intent i = new Intent(ExaminationActivity.this, FullScreenViewActivity.class);
                    i.putExtra("examination", currentExamination);
                    startActivityForResult(i, FULLSCREEN_REQUEST_CODE);
                }
            }
        });
    }

    private boolean idIsValidated() {
        return currentExamination.getPatientName().length() > 0;
    }

    private void updateElements() {
        if (!session.isValid()) {
            idTextView.setText("Patient ID: *******");
            firstNameTextView.setText("Name: not available in offline mode");
            lastNameTextView.setText("Name: not available in offline mode");
            idTextView.setText("*******");
        } else {
            idTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.patient_id) + "</b> " +
                    currentExamination.getPatientSsn()));
            lastNameTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.last_name) + "</b> " +
                    currentExamination.getPatientName()));
            firstNameTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.first_name) + "</b> " +
                    currentExamination.getPatientName()));
        }

        // TODO: show birth date
        //dateOfBirthTextView.setText();

        // Count number of images without comment
        int imagesWithoutComment = 0;
        for (UltrasoundImage usi : currentExamination.getUltrasoundImages()) {
            if (usi.getComment().equals(" ") || usi.getComment().isEmpty())
                imagesWithoutComment++;
        }

        imagesWithoutCommentTextView.setText(imagesWithoutComment + " image(s) without comment");

        // Reset font color
        imagesWithoutCommentTextView.setTextColor(Color.BLACK);

        if (imagesWithoutComment > 0)
            imagesWithoutCommentTextView.setTextColor(getResources().getColor(R.color.red));

    }

    /**
     * Returns the type of action this activity was started to do
     */
    private String activityStartedForAction() {
        // get intent from last activity
        Intent intent = getIntent();
        // Check what kind of extras intent has
        if (intent.hasExtra("chosen_images") && intent.hasExtra("info")) {
            // Activity was started to add a new examination
            return "new_examination";
        } else if (intent.hasExtra("ex_id")) {
            // Activity was started to edit an examination
            return "edit_examination";
        } else if (intent.hasExtra("examination")) {
            // Activity was started to edit an examinationObject
            return "edit_examinationObject";
        }

        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            ArrayList<String> info = data.getStringArrayListExtra("patient");
            if (info.size() > 1) {
                currentExamination.setPatientSsn(info.get(0));
                currentExamination.setPatientName(info.get(1));
            }
            initFirstViewElements();
        }
        if (resultCode == RESULT_OK && requestCode == FULLSCREEN_REQUEST_CODE) {
            currentExamination = data.getParcelableExtra("examination");
            updateElements();
        }
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
        updateSession();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSession();
    }

    private void updateSession() {
        if (session.isValid()) {
            session.updateSession();
        }
    }
}
