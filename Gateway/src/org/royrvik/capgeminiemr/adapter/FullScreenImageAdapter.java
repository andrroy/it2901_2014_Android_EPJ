package org.royrvik.capgeminiemr.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.royrvik.capgeminiemr.R;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.database.DatabaseHelper;
import org.royrvik.capgeminiemr.utils.BitmapUtils;
import org.royrvik.capgeminiemr.utils.SessionManager;
import org.royrvik.capgeminiemr.utils.UpdateDatabaseTask;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FullScreenImageAdapter extends PagerAdapter{

    private Examination currentExamination;
    private Context context;
    private ImageView imageView;
    private PhotoView photoView;
    private LayoutInflater inflater;
    private PhotoViewAttacher mAttacher;
    private TextView commentTextView;
    private final static int IMAGE_HEIGHT = 300;
    private final static int IMAGE_WIDTH = 300;
    private int currentImage;
    private DialogFragment newFragment;
    private DatabaseHelper dbHelper;
    private SessionManager session;


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
        session =  new SessionManager(context);
        photoView = new PhotoView(container.getContext());

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false);

        imageView = (ImageView) viewLayout.findViewById(R.id.imgDisplay);

        final Button closeButton = (Button) viewLayout.findViewById(R.id.btnClose);
        final Button deleteButton = (Button) viewLayout.findViewById(R.id.btnDelete);
        final Button commentButton = (Button) viewLayout.findViewById(R.id.btnComment);

        dbHelper = DatabaseHelper.getInstance(context, session.getDatabaseInfo());

        // Display image data
        File file = new File(currentExamination.getAllImages().get(position));
        Date fileDate = new Date(file.lastModified());
        TextView imageDataView = (TextView) viewLayout.findViewById(R.id.imageData);
        String date = new SimpleDateFormat("'Captured' EEEE dd.MM.yyyy HH:mm").format(fileDate);
        imageDataView.setText(date + "");

        imageView.setImageBitmap(BitmapUtils.
                decodeSampledBitmapFromStorage(currentExamination.getAllImages().get(position),
                        IMAGE_WIDTH, IMAGE_HEIGHT));

        mAttacher = new PhotoViewAttacher(imageView);

        // Button clicklisteners
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("APP:", "Close button clicked");
                dbHelper.updateExamination(currentExamination);
                // new UpdateDatabaseTask(session, context, currentExamination).execute();

                Log.d("APP:", "Done updating, closing view");

                Intent returnIntent = new Intent();
                returnIntent.putExtra("examination", currentExamination);
                ((Activity)context).setResult(Activity.RESULT_OK, returnIntent);
                ((Activity) context). finish();
                Log.d("APP:", "Done closing - finish");

            }
        });


        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentImage = position;
                commentButton.setBackgroundResource(R.drawable.ic_comment);

                // showCommentDialog();
                // Custom Dialog
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_comment);

                // Set the custom dialog components - text and button
                commentTextView = (TextView) dialog.findViewById(R.id.commentEditText);
                if (!currentExamination.getUltrasoundImages().get(position).getComment().isEmpty()) {

                    if(currentExamination.getUltrasoundImages().get(position).getComment().equals(" "))
                        commentTextView.setText("");
                    else
                        commentTextView.append(currentExamination.getUltrasoundImages().get(position).getComment());
                }

                commentTextView.setFocusable(true);

                Button dialogSave = (Button) dialog.findViewById(R.id.dialogButtonOK);
                Button dialogCancel = (Button) dialog.findViewById(R.id.dialogCancel);

                dialogSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveComment(position, commentTextView.getText().toString());
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

        commentButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                commentButton.setBackgroundResource(R.drawable.ic_comment_down);
                return false;
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

    private void saveComment(int index, String comment) {
            session.updateSession();
        currentExamination.getUltrasoundImages().get(index).setComment(comment);
    }

    public void deleteImage(int index) {
        session.updateSession();
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

    private void updateSession() {
        if (session.isValid()) {
            session.updateSession();
        }
    }
}