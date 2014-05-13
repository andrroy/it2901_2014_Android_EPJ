package org.royrvik.capgeminiemr;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
import org.royrvik.capgeminiemr.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

public class ExaminationActivity extends ActionBarActivity {

    private static final int REQUEST_CODE = 5;
    private static final int FULLSCREEN_REQUEST_CODE = 15;
    private static final int CHANGEID_REQUEST_CODE = 6;
    private TextView idTextView, firstNameTextView, lastNameTextView,
            imagesWithoutCommentTextView, dateOfBirthTextView, examinationCommentTextView, examDateTextView,
            examinationNumberTextView;
    private ImageButton editIDImageButton, editExamCommentButton;
    private ImageView isVerifiedImageView;
    private Button viewImagesButton, reviewAndUploadButton;
    private Examination currentExamination;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private ArrayList<String> incomingImages;

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

        //Getting the session
        session = new SessionManager(getApplicationContext());
        dbHelper = DatabaseHelper.getInstance(this, session.getDatabaseInfo());

        // Check where the activity was launched from and choose appropriate action based on result
        Intent intent = getIntent();
        // Activity was started to create a new examination
        if (activityStartedForAction().equals("new_examination")) {
            Log.d("APP:", "Examination: NEW EXAMINATION");
            incomingImages = intent.getStringArrayListExtra("chosen_images");
            ArrayList<String> infoArrayList = intent.getStringArrayListExtra("info");
            currentExamination = new Examination();
            if (infoArrayList.size() < 2) {
                currentExamination.setPatientFirstName("");
            } else {
                currentExamination.setPatientFirstName(infoArrayList.get(2));
                currentExamination.setPatientLastName(infoArrayList.get(3));
                currentExamination.setExaminationNumber(Integer.parseInt(infoArrayList.get(5)));
                currentExamination.setExaminationTime(Long.parseLong(infoArrayList.get(6)));
            }
            currentExamination.setPatientSsn(infoArrayList.get(1));
            for (String uri : incomingImages) {
                currentExamination.addUltrasoundImage(new UltrasoundImage(uri));
            }
        }

        // Activity was started to edit an existing examination
        else if (activityStartedForAction().equals("edit_examination")) {
            Log.d("APP:", "Examination: EDIT EXAMINATION");
            int exId = intent.getIntExtra("ex_id", -1);
            if (exId != -1) {
                currentExamination = dbHelper.getExamination(exId); // This should never be used - Rix1
            } else finish();
        } else if (activityStartedForAction().equals("edit_examinationObject")) {
            currentExamination = intent.getParcelableExtra("examination");
        }

        // Initialize GUI elements
        initViewElements();
        idIsValidated();
    }

    private void initViewElements() {
        idTextView = (TextView) findViewById(R.id.examSSNtextView);
        firstNameTextView = (TextView) findViewById(R.id.examPatientFirstNameTextView);
        lastNameTextView = (TextView) findViewById(R.id.examPatientLastNameTextView);
        imagesWithoutCommentTextView = (TextView) findViewById(R.id.imagesWithoutCommentTextView);
        examDateTextView = (TextView) findViewById(R.id.examDateTextView);
        editIDImageButton = (ImageButton) findViewById(R.id.editIDImageButton);
        editExamCommentButton = (ImageButton) findViewById(R.id.editExamCommentButton);
        dateOfBirthTextView = (TextView) findViewById(R.id.examPatientDobTextView);
        isVerifiedImageView = (ImageView) findViewById(R.id.isVerifiedImageView);
        examinationCommentTextView = (TextView)findViewById(R.id.examCommentTextView);
        examinationNumberTextView = (TextView)findViewById(R.id.examExamNumberTextView);

        examinationNumberTextView.setText(getResources().getString(R.string.exam) + " " + currentExamination.getExaminationNumber());



        View.OnClickListener changeID = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ExaminationActivity.this, IdentifyPatientActivity.class);
                i.putExtra("examination", currentExamination);
                saveData();
                startActivityForResult(i, CHANGEID_REQUEST_CODE);
            }
        };


        editIDImageButton.setOnClickListener(changeID);
        idTextView.setOnClickListener(changeID);

        //Updates the verification imageview.
        idIsValidated();

        reviewAndUploadButton = (Button) findViewById(R.id.reviewUploadButton);
        reviewAndUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!idIsValidated()){
                    Log.d("APP:", "Examination: DU skal avslutte her");
                    return;
                }

                saveData();

                // Start ReviewUpload and add the examination id as an extra in the intent
                Intent i = new Intent(ExaminationActivity.this, ReviewUploadActivity.class);
                i.putExtra("examination", currentExamination);
                startActivity(i);
                finish();
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

        View.OnClickListener examinationCommentListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Custom Dialog
                final Dialog dialog = new Dialog(ExaminationActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_comment);

                final TextView commentTextView = (TextView) dialog.findViewById(R.id.commentEditText);
                if (currentExamination.getExaminationComment() != null)
                    commentTextView.setText(currentExamination.getExaminationComment());

                commentTextView.setFocusable(true);

                Button dialogSave = (Button) dialog.findViewById(R.id.dialogButtonOK);
                Button dialogCancel = (Button) dialog.findViewById(R.id.dialogCancel);

                dialogSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentExamination.setExaminationComment(commentTextView.getText().toString());
                        dialog.dismiss();
                        updateExaminationElements();
                    }
                });

                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        };

        editExamCommentButton.setOnClickListener(examinationCommentListener);
        examinationCommentTextView.setOnClickListener(examinationCommentListener);

    }

    /**
     * I just want to state that I am profoundly sorry if this creates a lot of extra work.
     * I admit that it is thought through.
     * @return true if validated (on the basis of the lenght of the firstname, lol) false if not
     */

    private boolean idIsValidated() {
        if(currentExamination.getPatientFirstName().length() > 0) {
            isVerifiedImageView.setImageResource(R.drawable.ic_navigation_accept);
            lastNameTextView.setVisibility(View.VISIBLE);
            firstNameTextView.setVisibility(View.VISIBLE);
            updateElements();
            return true;
        }
        updateExaminationElements();
        lastNameTextView.setVisibility(View.GONE);
        firstNameTextView.setVisibility(View.GONE);
        dateOfBirthTextView.setText(Html.fromHtml("<i>" + getResources().getString(R.string.not_found) + "</i>"));
        isVerifiedImageView.setImageResource(R.drawable.ic_navigation_cancel);
        return false;
    }

    private void updateElements(){
        updatePersonElements();
        updateExaminationElements();
    }

    private void updatePersonElements() {
        if (!session.isValid()) {
            idTextView.setText("Patient ID: *******");
            firstNameTextView.setText("Name: not available in offline mode");
            lastNameTextView.setText("Name: not available in offline mode");
            idTextView.setText("*******");
        } else {
            idTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.patient_id) + "</b> " +
                    currentExamination.getPatientSsn()));
            lastNameTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.last_name) + "</b> " +
                    currentExamination.getPatientLastName()));
            firstNameTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.first_name) + "</b> " +
                    currentExamination.getPatientFirstName()));
        }

        dateOfBirthTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.date_of_birth) + "</b> " +
                Utils.ssnToDateOfBirth(currentExamination.getPatientSsn())));
    }
    private void updateExaminationElements(){
        // Check if examination has a comment
        if(currentExamination.getExaminationComment() == null) {
            examinationCommentTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.exam_comment) + "</b> " +
                    ""));
        }
        else {
            examinationCommentTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.exam_comment) + "</b> " +
                    currentExamination.getExaminationComment()));
        }

        examDateTextView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.conducted) + "</b> " +
                Utils.formattedDate(currentExamination.getExaminationTime())));


        // Count number of images without comment
        int imagesWithoutComment = 0;
        for (UltrasoundImage usi : currentExamination.getUltrasoundImages()) {
            if (usi.getComment().equals(" ") || usi.getComment().isEmpty())
                imagesWithoutComment++;
        }

        if(imagesWithoutComment == 1)
            imagesWithoutCommentTextView.setText(imagesWithoutComment + " image without comment");
        else
            imagesWithoutCommentTextView.setText(imagesWithoutComment + " images without comment");


        // Reset font color
        imagesWithoutCommentTextView.setTextColor(Color.BLACK);

        if (imagesWithoutComment > 0) {
            imagesWithoutCommentTextView.setTextColor(getResources().getColor(R.color.red));
        }
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
                currentExamination.setPatientFirstName(info.get(1));
            }
            initViewElements();
            idIsValidated();
        }
        if (resultCode == RESULT_OK && requestCode == FULLSCREEN_REQUEST_CODE) {
            currentExamination = data.getParcelableExtra("examination");
            idIsValidated();
        }
        if(resultCode == RESULT_OK && requestCode == CHANGEID_REQUEST_CODE){
            Log.d("APP:", "Examination: Recieved updated examination from IdentifyActivity");
            currentExamination = data.getParcelableExtra("examination");
            // Saves data here because IdentifyPatient does not have support for saving examinations (as of now)
            saveData();
            idIsValidated();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back button in actionbar clicked. Exit activity and open previous in activity stack
                saveData();
                startActivity(new Intent(this, HomeScreenActivity.class));
                finish();
                break;
        }
        return true;
    }

    // Choose action based on why this activity was started
    // TODO: this method should initiate an async task
    private void saveData(){
        int dbID;
        if (currentExamination.getId() == -1) {
            dbID = dbHelper.addExamination(currentExamination);
            currentExamination.setId(dbID);
        } else {
            dbHelper.updateExamination(currentExamination);
        }
    }

    @Override
    public void onBackPressed(){
        // Choose action based on why this activity was started
        saveData();
        startActivity(new Intent(this, HomeScreenActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        idIsValidated();
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
    @Override
    protected void onPause(){
        super.onPause();
        // We cannot pause this because it is waiting for onActivityResult()...
        // saveData();
        // finish();
    }
}