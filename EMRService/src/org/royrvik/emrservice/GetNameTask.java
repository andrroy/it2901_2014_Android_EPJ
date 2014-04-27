package org.royrvik.emrservice;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.*;

public class GetNameTask extends AsyncTask<String, Void, String> {

    //private static final String server = "jdbc:mysql://mysql.stud.ntnu.no:3306/rikardbe_emrDB";
    private static final String server = "jdbc:mysql://royrvik.org:3306/rikardbe_emrDB";
    private String username;
    private String password;

    private static final String driver = "com.mysql.jdbc.Driver";
    private static String query;
    private String ssn;


    public GetNameTask(String ssn, String username, String password){
        this.ssn = ssn;
        this.username = username;
        this.password = password;

    }

    protected String doInBackground(String... pid){

        String name = null;

        try {
            Class.forName(driver).newInstance();
            Connection conn = DriverManager.getConnection(server, username, password);

            query = "SELECT name FROM patients WHERE pid=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, ssn); //change to Input

            ResultSet results = statement.executeQuery();
            while (results.next()){
                name = results.getString(1);
            }
            results.close();
            statement.close();
            conn.close();

        } catch (InstantiationException e) {
            e.printStackTrace();
            Log.d("APP", "Error:::::::: " + e.toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.d("APP", "Error:::::::: " + e.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d("APP", "Error:::::::: " + e.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("APP", "Error:::::::: " + e.toString());
        }

        return name;
    }
}

