package org.royrvik.vscanlauncher;

import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.cengalabs.flatui.FlatUI;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class LauncherActivity extends ActionBarActivity {

    private static final String TAG = "APP";
    private static int RESULT_LOAD_IMAGE = 1;
    private static final String BROADCAST_CODE = "find a better code?";

    private Button newExaminationBtn, launchGatewayBtn, settingsBtn;

    private ReceiveMessages receiver = null;
    private ArrayList<String> selectedImagesPath;
    private ArrayList<String> patientData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.setDefaultTheme(FlatUI.ORANGE);
        setContentView(R.layout.main_test);

        // Actionbar style
        FlatUI.setActionBarTheme(this, FlatUI.DARK, false, true);
        getSupportActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(FlatUI.DARK, false));
        getActionBar().setTitle(Html.fromHtml("<font color=\"#f2f2f2\">" + getResources().getString(R.string.app_name)
                + "</font>"));

        updateImageLibrary();

        selectedImagesPath = new ArrayList<String>();
        patientData = new ArrayList<String>();
        receiver = new ReceiveMessages();
        registerReceiver(receiver, new IntentFilter(BROADCAST_CODE));

        newExaminationBtn = (Button) findViewById(R.id.btn_newExamination);
        newExaminationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LauncherActivity.this);
                builder.setTitle("Identify patient?");
                builder.setMessage("Do you wish to identify the patient before the examination?");
                builder.setIcon(R.drawable.ic_addpatient);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startIdentifyPatient();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(LauncherActivity.this, ScannerActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
                builder.show();
            }
        });

        launchGatewayBtn = (Button) findViewById(R.id.btn_gateway);
        launchGatewayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchHomeScreen();
            }
        });

        settingsBtn = (Button) findViewById(R.id.btn_settings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: DO nothing, we don't got any settings, lol.
            }
        });
    }

    /**
     * Launch straight to the home screen of Gateway.
     */
    private void launchHomeScreen() {
        new EMRLauncher(getApplicationContext()).start();
    }

    /**
     * Launches the main EMR application with images.
     */
    private void startOtherApplication() {
        if (selectedImagesPath.size() > 0)
            new EMRLauncher(getApplicationContext(), selectedImagesPath).start(); //Images are chosen
        else
            Toast.makeText(getApplicationContext(), "Please select images", Toast.LENGTH_SHORT).show(); //No images chosen
    }

    /**
     * Launches the main EMR application with images and ID
     */
    private void startOtherAppWithId() {
        if (patientData.size() > 1) {
            if (selectedImagesPath.size() > 0)
                new EMRLauncher(getApplicationContext(), selectedImagesPath, patientData).start(); //Images are chosen
            else
                Toast.makeText(getApplicationContext(), "Please select images", Toast.LENGTH_SHORT).show(); //No images chosen
        } else Toast.makeText(getApplicationContext(), "No ID available.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Launches the main EMR application to identify a patient
     */
    private void startIdentifyPatient() {
        new EMRLauncher(getApplicationContext(), BROADCAST_CODE).start();
    }

    /**
     *
     */
    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     * Adds test images to the device
     */
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

//        if (!fileList.contains("ultrasound5"))
//            saveImage(image5, "ultrasound5.jpg");
//
//        if (!fileList.contains("ultrasound6"))
//            saveImage(image6, "ultrasound6.jpg");
/*
        if (!fileList.contains("space1"))
            saveImage(image7, "space1.png");

        if (!fileList.contains("space2"))
            saveImage(image8, "space2.png");
*/
        // Refresh the image gallery
        // This does not work on Android 4.4+. We catch the exception, but other than that we do nothing.
//        try {
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//        } catch (SecurityException secException) {
//            Log.d(TAG, "KitKat doesn't like this :(");
//        }

        /*MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.w(TAG, "Does it work?");
            }
        });*/
    }

    /**
     * @param context
     * @param contentUri Uri to get absolute path from
     * @return absolute path to a file
     */
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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

    private class ReceiveMessages extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> data = intent.getStringArrayListExtra("patient");
            if (data != null && data.size() > 0) {
                patientData = data;
                Intent i = new Intent(LauncherActivity.this, ScannerActivity.class);
                i.putStringArrayListExtra("patientData", patientData);
                startActivity(i);

            } else patientData.add("No ID available.");
            // patientIdTextView.setText(patientData.get(1));
            if (patientData.size() > 1) {
                Toast.makeText(LauncherActivity.this, "ID received", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LauncherActivity.this, "No data received, check connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}