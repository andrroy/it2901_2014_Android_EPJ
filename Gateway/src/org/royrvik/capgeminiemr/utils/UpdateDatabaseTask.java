package org.royrvik.capgeminiemr.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.database.DatabaseHelper;
import org.royrvik.capgeminiemr.utils.SessionManager;

/**
 * Created by rikardeide on 14/5/14.
 */
public class UpdateDatabaseTask extends AsyncTask<String, Void, Boolean> {

    private DatabaseHelper dbHelper;
    private SessionManager session;
    private Context context;
    private Examination examination;

    public UpdateDatabaseTask(SessionManager session, Context context, Examination examination){
        this.session = session;
        this.context = context;
        this.examination = examination;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Log.d("APP:", "AsyncTask: Writing to database...");
        dbHelper = DatabaseHelper.getInstance(context, session.getDatabaseInfo());
        dbHelper.updateExamination(examination);
        return null;
    }
}
