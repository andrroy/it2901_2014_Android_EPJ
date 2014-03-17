package org.royrvik.capgeminiemr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.data.UltrasoundImage;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "emrdb";

    // Table name
    private static final String TABLE_EXAMINATION = "examination";
    private static final String TABLE_ULTRASOUNDIMAGE = "ultrasoundimage";

    // Column names
    // Examination
    private static final String KEY_EX_ID = "examination_id";
    private static final String KEY_PATIENT_NAME = "patient_name";
    private static final String KEY_SSN = "patient_ssn";

    // Ultrasoundimage
    private static final String KEY_USI_ID = "ultrasoundimage_id";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_URI = "uri";


    private static final String[] COLUMNS_EX = {KEY_EX_ID, KEY_PATIENT_NAME, KEY_SSN};
    private static final String[] COLUMNS_USI = {KEY_USI_ID, KEY_EX_ID, KEY_URI, KEY_COMMENT};

    private static final String TAG = "APP";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_EXAMINATION_TABLE = "CREATE TABLE examination ( " +
                "examination_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "patient_name TEXT, " +
                "patient_ssn INTEGER )";

        // Create examination table
        db.execSQL(CREATE_EXAMINATION_TABLE);

        String CREATE_ULTRASOUNDIMAGE_TABLE = "CREATE TABLE ultrasoundimage ( " +
                "ultrasoundimage_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "examination_id INTEGER, " +
                "comment TEXT, " +
                "uri TEXT, " +
                "FOREIGN KEY(examination_id) REFERENCES examination(examination_id) )";

        // Create ultrasoundimage table
        db.execSQL(CREATE_ULTRASOUNDIMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table
        db.execSQL("DROP TABLE IF EXISTS examination");
        db.execSQL("DROP TABLE IF EXISTS ultrasoundimage");


        // Recreate the table
        this.onCreate(db);
    }


    /*
            CRUD OPERATIONS
     */

    public void addExamination(Examination ex) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Build query
        ContentValues values = new ContentValues();
        values.put(KEY_SSN, ex.getPatientSsn());
        values.put(KEY_PATIENT_NAME, ex.getPatientName());

        // Execute query and get the auto incremented id value
        int examinationId = safeLongToInt(db.insert(TABLE_EXAMINATION, null, values));

        // Add all UltrasoundImages from the Examination to the Ultrasoundimage table
        for (UltrasoundImage usi : ex.getUltrasoundImages()) {

            ContentValues ultrasoundImageValues = new ContentValues();
            ultrasoundImageValues.put(KEY_EX_ID, examinationId);
            ultrasoundImageValues.put(KEY_COMMENT, usi.getComment());
            ultrasoundImageValues.put(KEY_URI, usi.getImageUri());

            // Execute query
            db.insert(TABLE_ULTRASOUNDIMAGE, null, ultrasoundImageValues);

        }

        db.close();

    }

    public Examination getExamination(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EXAMINATION, COLUMNS_EX, " examination_id = ?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // Build Examination with result
        Examination examination = new Examination();
        int exId = Integer.parseInt(cursor.getString(0));
        examination.setPatientName(cursor.getString(1));
        examination.setPatientSsn(Integer.parseInt(cursor.getString(2)));
        examination.setUltrasoundImages(getAllUltrasoundImagesFromExamination(exId));

        db.close();

        return examination;

    }

    public ArrayList<UltrasoundImage> getAllUltrasoundImagesFromExamination(int id) {

        ArrayList<UltrasoundImage> usiList = new ArrayList<UltrasoundImage>();
        String selectQuery = "SELECT * FROM ultrasoundimage WHERE examination_id=" + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                // Fetch the values and create a new Ultrasoundimage object
                UltrasoundImage usi = new UltrasoundImage();

                usi.setComment(cursor.getString(2));
                //Log.d(TAG, "comment" + cursor.getString(2));
                usi.setImageUri(cursor.getString(3));
                //Log.d(TAG, "uri" + cursor.getString(3));

                // Add the USI to the list
                usiList.add(usi);

            } while(cursor.moveToNext());
        }

        return usiList;
    }


    /*
            Helper methods for converting string arrays to/from string
     */
    public static String convertArrayToString(String[] array) {
        String stringSeparator = "__,__";
        String str = "";
        for (int i = 0; i < array.length; i++) {
            str = str + array[i];
            // Do not append comma at the end of last element
            if (i < array.length - 1) {
                str = str + stringSeparator;
            }
        }
        return str;
    }

    public static String[] convertStringToArray(String str) {
        String stringSeparator = "__,__";
        String[] arr = str.split(stringSeparator);
        return arr;
    }


    /**
     * @param l long to be converted
     * @return long as integer if possible
     */
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }


}
