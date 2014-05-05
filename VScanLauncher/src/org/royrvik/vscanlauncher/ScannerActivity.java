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
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
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
    private ArrayList<String> patientdData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        final Button uploadButton = (Button) findViewById(R.id.dummy_button);

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

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGatewayApp();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
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
        delayedHide(100);
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

    private void startGatewayApp(){
        //TODO: Generate arraylists
        ArrayList<String> imagePaths = null;
        //imagePaths = getImageURIs();

        Log.d("APP", "HEI ANDREAS");
        updateImageLibrary();
        imagePaths = getAllImages();

        String root = Environment.getExternalStorageDirectory().toString();
        int iterator = 0;
        for(String f : getAllImages()){
            f = root + "/DCIM/Camera/" + f;
            imagePaths.set(iterator++,f);
        }
        Log.d("APP", "LOL: " + imagePaths.toString());

        if(patientdData == null){
            new EMRLauncher(getApplicationContext(), imagePaths).start();
            finish();
        }
        else{
            new EMRLauncher(getApplicationContext(), imagePaths, patientdData).start();
            finish();
        }
    }



    /* ANDREAS TESTER */
    private void updateImageLibrary() {

        Bitmap image1 = BitmapFactory.decodeResource(getResources(), R.drawable.ultrasound1);
        Bitmap image2 = BitmapFactory.decodeResource(getResources(), R.drawable.ultrasound2);
        Bitmap image3 = BitmapFactory.decodeResource(getResources(), R.drawable.ultrasound3);
        Bitmap image4 = BitmapFactory.decodeResource(getResources(), R.drawable.ultrasound4);
//        Bitmap image5 = BitmapFactory.decodeResource(getResources(), R.drawable.ultrasound5);
//        Bitmap image6 = BitmapFactory.decodeResource(getResources(), R.drawable.ultrasound6);
        //Bitmap image7 = BitmapFactory.decodeResource(getResources(), R.drawable.space1);
        //Bitmap image8 = BitmapFactory.decodeResource(getResources(), R.drawable.space2);

        ArrayList<String> fileList = getAllImages();

        // So we don't add duplicates of all images every time we launch the application
        if (!fileList.contains("ultrasound1"))
            saveImage(image1, "ultrasound1.jpg");

        if (!fileList.contains("ultrasound2"))
            saveImage(image2, "ultrasound2.jpg");

        if (!fileList.contains("ultrasound3"))
            saveImage(image3, "ultrasound3.jpg");

        if (!fileList.contains("ultrasound4"))
            saveImage(image4, "ultrasound4.jpg");

    }


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

    private void saveImage(Bitmap finalBitmap, String name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/DCIM/Camera");
        myDir.mkdirs();
        File file = new File(myDir, name);
        Log.d("APP", "Se her RIkard: " + file.getAbsolutePath());
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
