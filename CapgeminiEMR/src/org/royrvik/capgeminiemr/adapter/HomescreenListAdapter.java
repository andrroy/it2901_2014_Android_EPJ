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
import org.royrvik.capgeminiemr.data.Examination;

import java.util.List;

public class HomescreenListAdapter extends ArrayAdapter<Examination> {

    private static final String TAG = "APP";

    private int resource;
    private LayoutInflater inflater;
    private Context context;

    public HomescreenListAdapter(Context context, int resourceId, List<Examination> objects) {
        super(context, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = (RelativeLayout) inflater.inflate(resource, null);

        //ultrasound image/data for THIS row
        Examination rowItem = getItem(position);

        ImageView rowImage = (ImageView) convertView.findViewById(R.id.usImageImageView);
        //Bitmap bitmap = BitmapFactory.decodeFile(rowItem.getImageUris().get(0));
        //rowImage.setImageBitmap(bitmap);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.usNameTextView);
        //nameTextView.setText(rowItem.getPatientName());
        nameTextView.setText("Navn");

        TextView dateTextView = (TextView) convertView.findViewById(R.id.usDateTextView);
        //dateTextView.setText(rowItem.getDate());
        dateTextView.setText("01.01.2014");

        return convertView;

    }
}
