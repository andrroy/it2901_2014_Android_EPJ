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

        ImageView rowImage = (ImageView) convertView.findViewById(R.id.homeListImageView);

        // Check if Examination has no images
        if(rowItem.getUltrasoundImages().size() < 1 || rowItem.getUltrasoundImages() == null) {
            // Show "no images available"
            int resID = context.getResources().getIdentifier("no_image" , "drawable", context.getPackageName());
            rowImage.setImageResource(resID);
        }
        else {
            Bitmap bitmap = BitmapFactory.decodeFile(rowItem.getUltrasoundImages().get(0).getImageUri());
            rowImage.setImageBitmap(bitmap);
        }

        TextView ssnTextView = (TextView) convertView.findViewById(R.id.homeListSsnTextView);
        ssnTextView.setText(rowItem.getPatientSsn());

        TextView nameTextView = (TextView) convertView.findViewById(R.id.homeListNameTextView);
        nameTextView.setText(rowItem.getPatientName());

        TextView dateTextView = (TextView) convertView.findViewById(R.id.homeListDateTextView);
        dateTextView.setText(rowItem.getDate());

        return convertView;

    }
}
