package org.royrvik.vscanlauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class LauncherActivity extends Activity {

    private static final String TAG = "APP";
    private static int RESULT_LOAD_IMAGE = 1;

    private Button launchOtherAppButton, openGalleryButton;
    private TextView numberofChosenImagesTV;

    private ArrayList<String> selectedImagesPath;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        selectedImagesPath = new ArrayList<String>();
        openGalleryButton = (Button) findViewById(R.id.openGalleryButton);
        numberofChosenImagesTV = (TextView) findViewById(R.id.imagesChosenTextView);

        Bitmap image1 = BitmapFactory.decodeResource(getResources(), R.drawable.ultrasound1);
        Bitmap image2 = BitmapFactory.decodeResource(getResources(), R.drawable.ultrasound2);
        Bitmap image3 = BitmapFactory.decodeResource(getResources(), R.drawable.ultrasound3);

        ArrayList<String> filListe = getAllImages();

        // So we don't add duplicates of all images every time we launch the application
        if (!filListe.contains("ultrasound1"))
            saveImage(image1, "ultrasound1.jpg");

        if (!filListe.contains("ultrasound2"))
            saveImage(image2, "ultrasound2.jpg");

        if (!filListe.contains("ultrasound3"))
            saveImage(image3, "ultrasound3.jpg");

        // Refresh the image gallery
        // This does not work on Android 4.4+. We catch the exception, but other than that we do nothing.
        try {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
        catch(SecurityException secException) {
            Log.w(TAG, "KitKat doesn't like this :(");
        }

        /*MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.w(TAG, "Does it work?");
            }
        });*/

        launchOtherAppButton = (Button) findViewById(R.id.launchOtherAppButton);
        launchOtherAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOtherApplication();
            }
        });

        openGalleryButton = (Button) findViewById(R.id.openGalleryButton);
        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

    }

    /**
     * Launches the main EMR application.
     */
    private void startOtherApplication() {
        Intent i = getPackageManager().getLaunchIntentForPackage("org.royrvik.capgeminiemr");
        i.putStringArrayListExtra("chosen_images", selectedImagesPath);
        startActivity(i);
    }


    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.TITLE};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            cursor.close();

            selectedImagesPath.add(getRealPathFromURI(this, selectedImage));
            numberofChosenImagesTV.setText("Number of selected images: " + Integer.toString(selectedImagesPath.size()));

        }

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
}