package org.royrvik.capgeminiemr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HomeListAdapter extends ArrayAdapter<UltrasoundRowItem> {

    private static final String TAG = "APP";

    private int resource;
    private LayoutInflater inflater;
    private Context context;

    public HomeListAdapter(Context ctx, int resourceId, List<UltrasoundRowItem> objects) {
        super(ctx, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(ctx);
        context = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = (RelativeLayout) inflater.inflate(resource, null);

        //ultrasound image for THIS row
        UltrasoundRowItem rowItem = getItem(position);

        Log.w(TAG, rowItem.getImageUri());
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
