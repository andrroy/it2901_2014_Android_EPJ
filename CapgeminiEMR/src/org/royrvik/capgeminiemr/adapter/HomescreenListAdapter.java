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

import java.util.List;

public class HomescreenListAdapter extends ArrayAdapter<UltrasoundRowItem> {

    private static final String TAG = "APP";

    private int resource;
    private LayoutInflater inflater;
    private Context context;

    public HomescreenListAdapter(Context context, int resourceId, List<UltrasoundRowItem> objects) {
        super(context, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = (RelativeLayout) inflater.inflate(resource, null);

        //ultrasound image/data for THIS row
        UltrasoundRowItem rowItem = getItem(position);

        ImageView rowImage = (ImageView) convertView.findViewById(R.id.usImageImageView);
        Bitmap bitmap = BitmapFactory.decodeFile(rowItem.getImageUri());
        rowImage.setImageBitmap(bitmap);

        TextView descriptionTextView = (TextView) convertView.findViewById(R.id.usDescriptionTextView);
        descriptionTextView.setText(rowItem.getDescription());

        TextView dateTextView = (TextView) convertView.findViewById(R.id.usDateTextView);
        dateTextView.setText(rowItem.getDate());

        return convertView;

    }
}
