package org.royrvik.capgeminiemr.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.royrvik.capgeminiemr.R;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.utils.BitmapUtils;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class FullScreenImageAdapter extends PagerAdapter {

    private Examination currentExamination;
    private Context context;
    private ImageView imageView;
    private PhotoView photoView;
    private LayoutInflater inflater;
    private PhotoViewAttacher mAttacher;
    private TextView commentTextView;
    private final static int IMAGE_HEIGHT = 300;
    private final static int IMAGE_WIDTH = 300;


    public FullScreenImageAdapter(Context context, Examination currentExamination) {
        this.context = context;
        this.currentExamination = currentExamination;
    }

    public int getCount() {
        return currentExamination.getAllImages().size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Object instantiateItem(final ViewGroup container, final int position) {

        photoView = new PhotoView(container.getContext());

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false);

        imageView = (ImageView) viewLayout.findViewById(R.id.imgDisplay);

        final Button closeButton = (Button) viewLayout.findViewById(R.id.btnClose);
        final Button deleteButton = (Button) viewLayout.findViewById(R.id.btnDelete);
        final Button commentButton = (Button) viewLayout.findViewById(R.id.btnComment);
        final Button tagButton = (Button) viewLayout.findViewById(R.id.btnTag);

        imageView.setImageBitmap(BitmapUtils.
                decodeSampledBitmapFromStorage(currentExamination.getAllImages().get(position),
                        IMAGE_WIDTH, IMAGE_HEIGHT));

        mAttacher = new PhotoViewAttacher(imageView);

        // Button clicklisteners
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) context).finish();
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentButton.setBackgroundResource(R.drawable.ic_comment);

                // Custom Dialog
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_comment);

                // Set the custom dialog components - text and button
                commentTextView = (TextView) dialog.findViewById(R.id.commentEditText);
                if (!currentExamination.getUltrasoundImages().get(position).getComment().isEmpty()) {
                    // Set comment
                    commentTextView.append(currentExamination.getUltrasoundImages().get(position).getComment());
                }

                commentTextView.setFocusable(true);

                Button dialogSave = (Button) dialog.findViewById(R.id.dialogButtonOK);
                Button dialogCancel = (Button) dialog.findViewById(R.id.dialogCancel);

                dialogSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveComment(position);
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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                deleteButton.setBackgroundResource(R.drawable.ic_delete_up);
                deleteImage(position);
            }
        });

        deleteButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                deleteButton.setBackgroundResource(R.drawable.ic_delete_down);
                return false;
            }
        });

        container.addView(viewLayout);

        return viewLayout;
    }

    private void saveComment(int index) {
        currentExamination.getUltrasoundImages().get(index).setComment(commentTextView.getText().toString());
    }

    public void deleteImage(int index) {

        if (currentExamination.getUltrasoundImages().size() <= 1) {
            Toast.makeText(context, "You can't delete your only image!", Toast.LENGTH_LONG).show();
        } else {
            currentExamination.deleteImage(index);
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
    }
}