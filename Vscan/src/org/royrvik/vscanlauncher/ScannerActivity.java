package org.royrvik.vscanlauncher;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.*;
import org.royrvik.vscanlauncher.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ScannerActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    private VideoView videoView;
    private static final int NUMBERofIMAGES = 3;
    private ArrayList<String> patientdData, examinationData;
    private final long EXAMINATION_TIME = System.currentTimeMillis() / 1000L;
    private int screenshots;
    private FrameLayout pnlFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        screenshots = 0;
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent.hasExtra("patientData"))
            this.patientdData = intent.getStringArrayListExtra("patientData");

        setContentView(R.layout.activity_scan);

        Toast.makeText(ScannerActivity.this, "New examination started", Toast.LENGTH_LONG).show();

        videoView = (VideoView) findViewById(R.id.fullscreen_content);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.samplevideo);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        videoView.start();

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        final Button uploadButton = (Button) findViewById(R.id.uploadBtn);
        final ImageView cameraIcon = (ImageView) findViewById(R.id.cameraIcon);
        final ImageView uploadIcon = (ImageView) findViewById(R.id.uploadIcon);
        pnlFlash = (FrameLayout) findViewById(R.id.pnlFlash);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });



        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });


        uploadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGatewayApp();
            }
        });

        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
        });


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.uploadBtn).setOnTouchListener(mDelayHideTouchListener);
    }

    private void takePhoto(){
        screenshots ++;
        pnlFlash.setVisibility(View.VISIBLE);

        AlphaAnimation fade = new AlphaAnimation(1, 0);
        fade.setDuration(50);
        fade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation anim) {
                pnlFlash.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        pnlFlash.startAnimation(fade);
        Toast.makeText(ScannerActivity.this, "Image captured", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        videoView.start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(500);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /**
     *
     * Prepeares neccesary data for the Gateway app.
     * This Method adds three types of data:
     *      1. Images for the current examination
     *      2. A (random) examination ID
     *      3. A timestamp for when the examination was started. This is declared when the Activity is started.
     */

    private void startGatewayApp(){
        ArrayList<String> imagePaths = new ArrayList<String>();
        examinationData = new ArrayList<String>();

        Random generator = new Random();
        int examinationNumber = generator.nextInt(10) + 1;

        updateImageLibrary();

        // Simulates taking photos by adding number of times pushed the camera button
        int append = 0;
        for (int i = 1; i<= screenshots; i++) {
            append++;
            if(append>7) { //Number of screenshots available
                append = 0;
            }
            imagePaths.add("/storage/emulated/0/DCIM/Camera/vscan_" + append + ".jpg");
        }

        Log.d("APP:", "Imagepaths: " + imagePaths.toString());

        if(patientdData == null){
            examinationData.add(Integer.toString(examinationNumber));
            examinationData.add(Long.toString(EXAMINATION_TIME));
            Log.d("APP:", "ExaminaionData: " + examinationData.toString());
            new EMRLauncher(getApplicationContext(), imagePaths, examinationData).start();
            finish();
        }
        else{
            examinationData.add(Integer.toString(examinationNumber));
            examinationData.add(Long.toString(EXAMINATION_TIME));
            Log.d("APP:", "PatientDATA: " + patientdData.toString());
            new EMRLauncher(getApplicationContext(), imagePaths, patientdData, examinationData).start();
            finish();
        }
    }

    private void updateImageLibrary() {

        Bitmap image1 = BitmapFactory.decodeResource(getResources(), R.drawable.vscan_1);
        Bitmap image2 = BitmapFactory.decodeResource(getResources(), R.drawable.vscan_2);
        Bitmap image3 = BitmapFactory.decodeResource(getResources(), R.drawable.vscan_3);
        Bitmap image4 = BitmapFactory.decodeResource(getResources(), R.drawable.vscan_4);
        Bitmap image5 = BitmapFactory.decodeResource(getResources(), R.drawable.vscan_5);
        Bitmap image6 = BitmapFactory.decodeResource(getResources(), R.drawable.vscan_6);
        Bitmap image7 = BitmapFactory.decodeResource(getResources(), R.drawable.vscan_7);

        ArrayList<String> fileList = getAllImages();

        // So we don't add duplicates of all images every time we launch the application
        if (!fileList.contains("vscan_1"))
            saveImage(image1, "vscan_1.jpg");

        if (!fileList.contains("vscan_2"))
            saveImage(image2, "vscan_2.jpg");

        if (!fileList.contains("vscan_3"))
            saveImage(image3, "vscan_3.jpg");

        if (!fileList.contains("vscan_4"))
            saveImage(image4, "vscan_4.jpg");

        if (!fileList.contains("vscan_5"))
            saveImage(image5, "vscan_5.jpg");

        if (!fileList.contains("vscan_6"))
            saveImage(image6, "vscan_6.jpg");

        if (!fileList.contains("vscan_7"))
            saveImage(image7, "vscan_7.jpg");
    }


    /**
     * @return an ArrayList of all image names as Strings
     */

    private ArrayList<String> getAllImages() {

        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA};
        Cursor c = null;
        SortedSet<String> dirList = new TreeSet<String>();

        ArrayList<String> allFileNames = new ArrayList<String>();

        String[] directories = null;
        if (u != null) {
            c = managedQuery(u, projection, null, null, null);
        }

        if ((c != null) && (c.moveToFirst())) {
            do {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try {
                    dirList.add(tempDir);
                } catch (Exception e) {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }

        for (int i = 0; i < dirList.size(); i++) {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if (imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if (imagePath.isDirectory()) {
                        imageList = imagePath.listFiles();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (File f : imageList)
                allFileNames.add(f.getName());

        }

        return allFileNames;

    }

    @Override
    public void onBackPressed(){
        Toast.makeText(ScannerActivity.this, "Examination aborted", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(ScannerActivity.this, LauncherActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * Takes a bitmap and saves it to /DCIM/Camera as a .jpeg file
     *
     * @param finalBitmap bitmap to be saved as .jpeg
     * @param name        name of image
     */

    private void saveImage(Bitmap finalBitmap, String name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/DCIM/Camera");
        myDir.mkdirs();
        File file = new File(myDir, name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
