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
import org.royrvik.capgeminiemr.utils.BitmapUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

public class ReviewListAdapter extends ArrayAdapter<UltrasoundImage> {

    private int resource;
    private LayoutInflater inflater;
    private Context context;
    private final String imageData = "image captured: ";

    public ReviewListAdapter(Context context, int resourceId, List<UltrasoundImage> objects) {
        super(context, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = (RelativeLayout) inflater.inflate(resource, null);

        //Data for THIS row
        UltrasoundImage rowItem = getItem(position);

        ImageView rowImage = (ImageView) convertView.findViewById(R.id.reviewImageImageView);

        File file = new File(rowItem.getImageUri());
        //TODO: You probably have to format the date
        Date filedDate = new Date(file.lastModified());
        TextView imageDataView = (TextView) convertView.findViewById(R.id.imageDate);
        imageDataView.setText(imageData + filedDate.toString());

        rowImage.setImageBitmap(BitmapUtils.decodeSampledBitmapFromStorage(rowItem.getImageUri(), 300, 300));

        TextView commentTextView = (TextView) convertView.findViewById(R.id.imageCommentTextView);
        commentTextView.setText(rowItem.getComment());

        return convertView;
    }
}
