package org.royrvik.capgeminiemr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.royrvik.capgeminiemr.R;
import org.royrvik.capgeminiemr.data.UltrasoundImage;
import org.royrvik.capgeminiemr.utils.BitmapUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReviewListAdapter extends ArrayAdapter<UltrasoundImage> {

    private int resource;
    private LayoutInflater inflater;

    public ReviewListAdapter(Context context, int resourceId, List<UltrasoundImage> objects) {
        super(context, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(context);
    }

    /**
     * Viewholder Pattern
     * In order to not load more views than necessary we reuse views.
     * This makes for better memory utilization and smoother scrolling in the listView
     */

    static class ViewHolder {
        TextView imageDataView;
        ImageView rowImage;
        TextView commentTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(resource, null);


            holder = new ViewHolder();
            holder.imageDataView = (TextView) convertView.findViewById(R.id.imageDate);
            holder.rowImage = (ImageView) convertView.findViewById(R.id.reviewImageImageView);
            holder.commentTextView = (TextView) convertView.findViewById(R.id.imageCommentTextView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UltrasoundImage rowItem = getItem(position);
        File file = new File(rowItem.getImageUri());

        Date fileDate = new Date(file.lastModified());
        String date = new SimpleDateFormat("'Captured' EEEE dd.MM.yyyy HH:mm").format(fileDate);

        holder.imageDataView.setText(date);
        holder.rowImage.setImageBitmap(BitmapUtils.decodeSampledBitmapFromStorage(rowItem.getImageUri(), 800, 100));
        holder.commentTextView.setText(rowItem.getComment());

        return convertView;
    }
}
