package org.royrvik.capgeminiemr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.royrvik.capgeminiemr.data.Examination;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "EMR_DB";

    // Table name
    private static final String TABLE_EXAMINATIONS = "examinations";

    // Column names
    private static final String KEY_ID = "id";
    private static final String KEY_PATIENT_NAME = "patient_name";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_IMAGES = "images";
    private static final String KEY_SSN = "ssn";

    private static final String[] COLUMNS = {KEY_ID, KEY_PATIENT_NAME, KEY_COMMENTS, KEY_IMAGES, KEY_SSN};


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_EXAMINATION_TABLE = "CREATE TABLE examination ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "patient_name TEXT, " +
                "comments TEXT, " +
                "ssn INTEGER, " +
                "images TEXT )";

        // Create examination table
        db.execSQL(CREATE_EXAMINATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table
        db.execSQL("DROP TABLE IF EXISTS examination");

        // Recreate the table
        this.onCreate(db);
    }


    /*
            CRUD operations
     */

    public void addExamination(int ssn, String[] images, String patientName, String[] comments) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SSN, ssn);
        values.put(KEY_PATIENT_NAME, patientName);
        values.put(KEY_COMMENTS, convertArrayToString(comments));
        values.put(KEY_IMAGES, convertArrayToString(images));

        db.insert(TABLE_EXAMINATIONS, null, values);

        db.close();

    }

    /*public Examination getExamination(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EXAMINATIONS, COLUMNS, " id = ?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Examination examination = new Examination(cursor.getString(0),
                cursor.getString(1), convertStringToArray(cursor.getString(2)));
    }*/

    // Helper methods for converting string arrays to/from string
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


}
