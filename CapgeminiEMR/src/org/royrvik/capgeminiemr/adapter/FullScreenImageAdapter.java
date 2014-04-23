package org.royrvik.capgeminiemr.adapter;

import android.view.MotionEvent;
import android.widget.Toast;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.royrvik.capgeminiemr.FullScreenViewActivity;
import org.royrvik.capgeminiemr.R;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import org.royrvik.capgeminiemr.data.Examination;
import org.royrvik.capgeminiemr.utils.TouchImageView;

/**
 * Created by rikardeide on 20/4/14.
 */

public class FullScreenImageAdapter extends PagerAdapter{

    private Activity _activity;
    private ArrayList<String> imageURIs;
    private LayoutInflater inflater;
    private Examination currentExamination;

    public FullScreenImageAdapter(Activity activity, ArrayList<String> uris){
        this._activity = activity;
        this.imageURIs = uris;
    }

    // Consctructor to support Examiniation obejcts when this is implemented.
    public FullScreenImageAdapter(Activity activity, Examination currentExamination){
        this._activity = activity;
        this.currentExamination = currentExamination;
        this.imageURIs = currentExamination.getAllImages();
    }

    public int getCount(){
        return this.imageURIs.size();
    }

    public boolean isViewFromObject(View view, Object object){
        return view == ((RelativeLayout) object);
    }

    public Object instantiateItem(ViewGroup container, int position){
        TouchImageView imgDisplay;
        final Button btnClose;
        final Button btnDelete;
        final Button btnComment;
        final Button btnTag;

        inflater = (LayoutInflater)_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false);

        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay); // layout_fullscreen_image;

        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
        btnDelete = (Button) viewLayout.findViewById(R.id.btnDelete);
        btnComment = (Button) viewLayout.findViewById(R.id.btnComment);
        btnTag = (Button) viewLayout.findViewById(R.id.btnTag);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imageURIs.get(position), options);
        imgDisplay.setImageBitmap(bitmap);


        /**
         * Button operations for close, delete and comment buttons.
         */

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _activity.finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                btnDelete.setBackgroundResource(R.drawable.ic_delete_up);
                // Delete image
                if (imageURIs.size() <= 1) {
                 //TODO: alert with Toast that the operation is not allowed
                }
                /**
                 *
                else {
                    currentExamination.deleteImage(currentImageId);
                    if (currentImageId > 0)
                        currentImageId--;
                    Crouton.makeText(this, "Image deleted", Style.CONFIRM).show();
                    updateEditorView();
                }
                 */
            }

        });

        btnDelete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                btnDelete.setBackgroundResource(R.drawable.ic_delete_down);
                return false;
            }
        });


        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}