package org.royrvik.emrservice;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.*;
import java.util.ArrayList;

public class GetNameTask extends AsyncTask<String, Void, ArrayList<String>> {

    private static final String server = "jdbc:mysql://royrvik.org:3306/rikardbe_emrDB";
    private String username;
    private String password;

    private static final String driver = "com.mysql.jdbc.Driver";
    private String ssn;


    public GetNameTask(String ssn, String username, String password){
        this.ssn = ssn;
        this.username = username;
        this.password = password;

    }

    protected ArrayList<String> doInBackground(String... args){

        String firstName = "";
        String lastName = "";
        String errorMessage = "";
        boolean didWork = false;

        try {
            Class.forName(driver).newInstance();
            Connection conn = DriverManager.getConnection(server, username, password);

            String query = "SELECT first_name, last_name FROM patients WHERE pid=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, ssn);

            ResultSet results = statement.executeQuery();
            while (results.next()){
                firstName = results.getString(1);
                lastName = results.getString(2);
            }
            results.close();
            statement.close();
            conn.close();
            didWork = true;

        }
        catch (InstantiationException e) { errorMessage = e.getMessage(); }
        catch (IllegalAccessException e) { errorMessage = e.getMessage(); }
        catch (ClassNotFoundException e) { errorMessage = e.getMessage(); }
        catch (SQLException e) { errorMessage = e.getMessage(); }


        /* Preparing returnMessage
        * ---------------------
        * Boolean didWork
        * SSN
        * FirstName
        * LastName
        * ErrorMessage
        */
        ArrayList<String> returnMessage = new ArrayList<String>();

        if(errorMessage != "" || firstName == "" || lastName == ""){
            didWork = false;
        }

        returnMessage.add(Boolean.toString(didWork));
        returnMessage.add(ssn);
        returnMessage.add(firstName);
        returnMessage.add(lastName);
        returnMessage.add(errorMessage);

        return returnMessage;

    }
}

