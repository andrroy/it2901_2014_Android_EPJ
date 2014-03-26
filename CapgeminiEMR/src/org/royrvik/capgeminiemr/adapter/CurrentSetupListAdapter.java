package org.royrvik.capgeminiemr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.royrvik.capgeminiemr.R;
import org.royrvik.capgeminiemr.data.SettingsItem;

import java.util.ArrayList;

public class CurrentSetupListAdapter extends ArrayAdapter<SettingsItem> {

    private static final String TAG = "APP";

    private int resource;
    private LayoutInflater inflater;
    private Context context;

    public CurrentSetupListAdapter(Context context, int resourceId, ArrayList<SettingsItem> objects) {
        super(context, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = (RelativeLayout) inflater.inflate(resource, null);

        // Data for THIS row
        SettingsItem rowItem = getItem(position);

        TextView keyTextView = (TextView) convertView.findViewById(R.id.currentSetupKeyTextView);
        keyTextView.setText(rowItem.getKey());

        TextView valueTextView = (TextView) convertView.findViewById(R.id.currentSetupValueTextView);
        valueTextView.setText(rowItem.getValue());

        return convertView;

    }
}