package org.royrvik.capgeminiemr.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import org.royrvik.capgeminiemr.R;
import org.royrvik.capgeminiemr.data.Examination;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.util.ArrayList;

/**
 * Created by rikardeide on 20/4/14.
 */

public class FullScreenImageAdapter extends PagerAdapter {

    private ArrayList<String> imageURIs;
    private Examination currentExamination;
    private Context mContext;
    private ImageView imageView;
    private PhotoView photoView;
    private LayoutInflater inflater;
    private PhotoViewAttacher mAttacher;
    private TextView text;


    public FullScreenImageAdapter(Context context, Examination currentExamination) {
        this.mContext = context;
        this.currentExamination = currentExamination;
        this.imageURIs = currentExamination.getAllImages();
        Log.d("APP", "CALLED CONSTRUCTOR");
    }

    public int getCount() {
        return imageURIs.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Object instantiateItem(final ViewGroup container, final int position) {
        final Button btnClose;
        final Button btnDelete;
        final Button btnComment;
        final Button btnTag;

        photoView = new PhotoView(container.getContext());

        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false);

        imageView = (ImageView) viewLayout.findViewById(R.id.imgDisplay);

        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
        btnDelete = (Button) viewLayout.findViewById(R.id.btnDelete);
        btnComment = (Button) viewLayout.findViewById(R.id.btnComment);
        btnTag = (Button) viewLayout.findViewById(R.id.btnTag);

        // TODO: Load a comprimized version of bitmap!

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap = BitmapFactory.decodeFile(imageURIs.get(position), options);

        // photoView.setImageBitmap(bitmap);
        imageView.setImageBitmap(bitmap);

        mAttacher = new PhotoViewAttacher(imageView);

        // Button clicklisteners
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) mContext).finish();
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnComment.setBackgroundResource(R.drawable.ic_comment);

                // Custom Dialog
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.setContentView(R.layout.dialog_test);

                // set the custom dialog components - text and button
                text = (TextView) dialog.findViewById(R.id.text);
                if (!getText(position).equals(null)) {
                    text.append(getText(0));
                }

                text.setFocusable(true);

                Button dialogSave = (Button) dialog.findViewById(R.id.dialogButtonOK);
                Button dialogCancel = (Button) dialog.findViewById(R.id.dialogCancel);

                dialogSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO: Save comment
                        save();

                        //currentExamination.getUltrasoundImages().get(currentImageId).setComment(commentEditText.getText().toString());
                        dialog.dismiss();
                    }
                });

                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                btnDelete.setBackgroundResource(R.drawable.ic_delete_up);
                Log.d("APP", "DELETE IMAGE POSITION " + position);
                deleteImage(position);
            }
        });

        btnDelete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                btnDelete.setBackgroundResource(R.drawable.ic_delete_down);
                return false;
            }
        });

        container.addView(viewLayout);
        //container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        return viewLayout;
    }

    private String getText(int pos) {
        return currentExamination.getUltrasoundImages().get(pos).getComment();
    }

    private void save() {
        //For testing purposes! It always comments the first image
        currentExamination.getUltrasoundImages().get(0).setComment(text.getText().toString());
    }

    public void deleteImage(int index) {
        //TODO: Need to figure out how to identify the image being viewed.
        // TODO: @dependency: what image is currently being viewed?

        if (imageURIs.size() <= 1) {
            Log.d("APP:", "You can't delete your only image!");
        } else {
            imageURIs.remove(index);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Log.d("APP", "destroyItem() " + position);
    }


}