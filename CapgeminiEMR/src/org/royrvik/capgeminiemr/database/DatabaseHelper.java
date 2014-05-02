package org.royrvik.capgeminiemr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.data.UltrasoundImage;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance = null;

    private static final int DATABASE_VERSION = 1;
    private String password;

    // Table names
    private static final String TABLE_EXAMINATION = "examination";
    private static final String TABLE_ULTRASOUNDIMAGE = "ultrasoundimage";

    // Column names
    // Examination
    private static final String KEY_EX_ID = "examination_id";
    private static final String KEY_PATIENT_NAME = "patient_name";
    private static final String KEY_SSN = "patient_ssn";
    private static final String KEY_DATE = "date";

    // Ultrasoundimage
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_URI = "uri";

    private static final String[] COLUMNS_EX = {KEY_EX_ID, KEY_PATIENT_NAME, KEY_SSN, KEY_DATE};

    private DatabaseHelper(Context context, ArrayList<String> databaseInfo) {
        super(context, databaseInfo.get(0), null, DATABASE_VERSION);
        this.password = databaseInfo.get(1);
    }

    public static synchronized DatabaseHelper getInstance(Context con, ArrayList<String> databaseInfo){
        if(instance == null){
            SQLiteDatabase.loadLibs(con);
            instance = new DatabaseHelper(con, databaseInfo);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_EXAMINATION_TABLE = "CREATE TABLE examination ( " +
                "examination_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "patient_name TEXT, " +
                "patient_ssn TEXT, " +
                "date TEXT )";

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

    public void logout() {
        instance.close();
        instance = null;
    }

    /**
     * Method to check if the database password is correct.
     * @return true if the password is correct.
     */
    public boolean checkDatabasePassword() {
        try {
            getReadableDatabase(password).close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Updates the database to use a new password.
     * @param oldPassword the previous password used with the database.
     * @return false if the old password was incorrect.
     */
    public boolean updateDatabasePassword(String oldPassword) {
        try {
            SQLiteDatabase db = getWritableDatabase(oldPassword);
            db.execSQL("PRAGMA key = '"+oldPassword+"'");
            db.execSQL("PRAGMA rekey = '"+password+"'");
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /*
            CREATE, READ, UPDATE, DELETE OPERATIONS
     */

    /**
     * Adds an Examination to the database. The ultrasoundimages for the Examination
     * is stored in a separate table to maintain database normalisation
     *
     * @param ex Examination to add
     * @return examination_id of the newly added examination
     */
    public int addExamination(Examination ex) {


        SQLiteDatabase db = this.getWritableDatabase(this.password);

        // Build query
        ContentValues values = new ContentValues();
        values.put(KEY_SSN, ex.getPatientSsn());
        values.put(KEY_PATIENT_NAME, ex.getPatientName());
        values.put(KEY_DATE, ex.getDate());

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
        return examinationId;

    }

    /**
     * Fetches an Examination
     *
     * @param id ID of examination to be fetched
     * @return Examination with this ID
     */
    public Examination getExamination(int id) {

        SQLiteDatabase db = this.getReadableDatabase(this.password);

        Cursor cursor = db.query(TABLE_EXAMINATION, COLUMNS_EX, " examination_id = ?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // Build Examination with result
        Examination examination = new Examination();
        int exId = Integer.parseInt(cursor.getString(0));
        examination.setId(exId);
        examination.setPatientName(cursor.getString(1));
        examination.setPatientSsn(cursor.getString(2));
        examination.setDate(cursor.getString(3));
        examination.setUltrasoundImages(getAllUltrasoundImagesFromExamination(exId));

        db.close();
        cursor.close();

        return examination;

    }

    /**
     * Deletes Examination with id from the database
     *
     * @param id ID of Examination to delete
     * @return true if an examination was deleted, false if not
     */
    public boolean deleteExamination(int id) {
        long t1 = System.currentTimeMillis();
        long t2, t3, t4;


        SQLiteDatabase db = this.getReadableDatabase(this.password);
        t2 = System.currentTimeMillis();
        t2 -= t1;
        t1 = System.currentTimeMillis();



        // Delete corresponding rows in TABLE_ULTRASOUNDIMAGE
        Log.d("APP", Integer.toString(db.delete(TABLE_ULTRASOUNDIMAGE, KEY_EX_ID + "=" + id, null)));

        t3 = System.currentTimeMillis();
        t3 -= t1;
        t1 = System.currentTimeMillis();

        // Delete row in TABLE_EXAMINATION
        boolean isDeleted = db.delete(TABLE_EXAMINATION, KEY_EX_ID + "=" + id, null) > 0;
        t4 = System.currentTimeMillis();
        t4 -= t1;

        Log.d("APP:", "Time to execute T4: " + t4 + " - T3: " + t3 + " t2: " + t2);
        db.close();
        return isDeleted;

    }

    /**
     * Updates an examination already stored in the database
     *
     * @param ex Examination to update
     */
    public void updateExamination(Examination ex) {


        SQLiteDatabase db = this.getReadableDatabase(this.password);

        // Build query
        ContentValues values = new ContentValues();
        values.put(KEY_SSN, ex.getPatientSsn());
        values.put(KEY_PATIENT_NAME, ex.getPatientName());
        values.put(KEY_DATE, ex.getDate());

        // Update examination row
        db.update(TABLE_EXAMINATION, values, KEY_EX_ID  + " = ?",
                new String[]{String.valueOf(ex.getId())});

        // Delete its corresponding images from the database
        db.delete(TABLE_ULTRASOUNDIMAGE, "examination_id=" + ex.getId(), null);

        int examinationId = ex.getId();

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

    /**
     * Deletes all examinations stored in the database
     */
    public void deleteAllExaminations() {


        SQLiteDatabase db = this.getReadableDatabase("test123");

        db.delete(TABLE_EXAMINATION, null, null);
        db.delete(TABLE_ULTRASOUNDIMAGE, null, null);
        db.close();
    }

    /**
     * Gets all Examinations stored in the database.
     *
     * @return ArrayList of all Examinations
     */
    public ArrayList<Examination> getAllExaminations() {


        SQLiteDatabase db = this.getReadableDatabase(this.password);

        Cursor cursor = db.rawQuery("select * from examination", null);

        ArrayList<Examination> examinationList = new ArrayList<Examination>();

        if (cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {
                // Get values from row
                int id = cursor.getInt(cursor.getColumnIndex("examination_id"));
                String name = cursor.getString(cursor.getColumnIndex("patient_name"));
                String ssn = cursor.getString(cursor.getColumnIndex("patient_ssn"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                // Get Ultrasoundimages from appropriate table for this Examination
                ArrayList<UltrasoundImage> usiList = getAllUltrasoundImagesFromExamination(id);
                // Create examination with data from this row
                Examination ex = new Examination(ssn, name, usiList, date);
                ex.setId(id);
                // Add it to the list
                examinationList.add(ex);

                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();

        return examinationList;
    }

    /**
     * Fetches all ultrasoundimages from the UltrasoundImage table with foreign key id.
     * Used by getExamination()
     *
     * @param id id of examination
     * @return Arraylist with UltrasoundImages for this Examination
     */
    public ArrayList<UltrasoundImage> getAllUltrasoundImagesFromExamination(int id) {


        SQLiteDatabase db = this.getReadableDatabase(this.password);

        ArrayList<UltrasoundImage> usiList = new ArrayList<UltrasoundImage>();
        String selectQuery = "SELECT * FROM ultrasoundimage WHERE examination_id=" + id;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // Fetch the values and create a new Ultrasoundimage object
                UltrasoundImage usi = new UltrasoundImage();

                usi.setComment(cursor.getString(2));
                //Log.d(TAG, "comment" + cursor.getString(2));
                usi.setImageUri(cursor.getString(3));
                //Log.d(TAG, "uri" + cursor.getString(3));

                // Add the USI to the list
                usiList.add(usi);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return usiList;
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
