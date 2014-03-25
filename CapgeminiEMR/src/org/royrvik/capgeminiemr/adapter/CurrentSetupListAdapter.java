package org.royrvik.capgeminiemr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.royrvik.capgeminiemr.R;
import org.royrvik.capgeminiemr.data.UltrasoundImage;

import java.util.ArrayList;

public class CurrentSetupListAdapter extends ArrayAdapter<ArrayList<String>> {

    private static final String TAG = "APP";

    private int resource;
    private LayoutInflater inflater;
    private Context context;

    public CurrentSetupListAdapter(Context context, int resourceId, ArrayList<ArrayList<String>> objects) {
        super(context, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = (RelativeLayout) inflater.inflate(resource, null);

        // Data for THIS row
        ArrayList<String> rowItem = getItem(position);

        TextView keyTextView = (TextView) convertView.findViewById(R.id.currentSetupKeyTextView);
        keyTextView.setText(rowItem.toString());

        TextView valueTextView = (TextView) convertView.findViewById(R.id.currentSetupValueTextView);
        valueTextView.setText(rowItem.toString());

        return convertView;

    }
}
