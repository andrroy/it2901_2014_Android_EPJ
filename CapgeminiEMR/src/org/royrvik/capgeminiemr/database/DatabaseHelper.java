package org.royrvik.capgeminiemr.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_EXAMINATION_TABLE = "CREATE TABLE examination ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "patient_name TEXT, " +
                "comments TEXT, " +
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

}
