package org.royrvik.capgeminiemr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class HomeScreenActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        final String[] imagesArray = new String[]{
                "Bilde1",
                "Bilde2",
                "Bilde3",

        };

        // Get the object reference of listview from the layout
        ListView listView = (ListView) findViewById(R.id.ultrasoundImagesListView);

        // Instantiating an array adapter for listview
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, imagesArray);

        listView.setAdapter(adapter);

        //Defining an item click listener
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {

                // AdapterView is the parent class of ListView
                ListView lv = (ListView) arg0;
                if (lv.isItemChecked(position)) {
                    Toast.makeText(getBaseContext(), imagesArray[position] + " valgt", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), imagesArray[position] + " valgt", Toast.LENGTH_SHORT).show();
                }

            }
        };

        // Setting the ItemClickEvent listener for the listview
        listView.setOnItemClickListener(itemClickListener);
    }
}
