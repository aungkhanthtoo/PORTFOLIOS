package com.akhh.aungkhanthtoo.androidforall;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.akhh.aungkhanthtoo.androidforall.data.MySQLiteHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class DetailActivity extends Activity {

    private static final String TAG = "DetailActivity";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";
    // View name of the image also.
    public static final String VIEW_NAME_IMAGE = "detail:image";

    // Base URL for images of vocab
    static final String BASE_URL = "https://firebasestorage.googleapis.com/v0/b/androidforall-ced0d.appspot.com/o/";

    // Position index for looping mImageArray to animate a slide show
    int mIndex = 0;

    private SQLiteOpenHelper mDBHelper;
    Runnable mRunnable;
    Handler mHandler;
    String[] mImageArray = new String[10];
    private int mDelayMillis = 3000;
    private String mId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setAllowEnterTransitionOverlap(true);
        setContentView(R.layout.activity_detail);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Roboto-ThinItalic.ttf");
        TextView titleTextView = findViewById(R.id.titleDetail);
        final TextView descripTextView = findViewById(R.id.description_text_detail);
        descripTextView.setTypeface(typeface);
        descripTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                descripTextView.setTextIsSelectable(true);
                return true;
            }
        });
        final ImageView detailImageView = findViewById(R.id.imageDetail);
        ViewCompat.setTransitionName(titleTextView, VIEW_NAME_HEADER_TITLE);
        ViewCompat.setTransitionName(detailImageView, VIEW_NAME_IMAGE);

        final Intent intent = getIntent();
        mId = getIntent().getStringExtra(MyListAdapter.EXTRA_ID);
        Log.d(TAG, "onCreate: MyListAdapter.EXTRA_ID "+mId);
        updateDatabase(mId, MySQLiteHelper.COLUMN_VISIT);
        //Get the placeholder color
        String hex = intent.getStringExtra(MyListAdapter.EXTRA_COLOR_HEX);
        int color = Color.parseColor(hex);

        String title = intent.getStringExtra(MyListAdapter.EXTRA_TITLE);
        titleTextView.setText(title);
        String sample = intent.getStringExtra(MyListAdapter.EXTRA_SAMPLE); // No TextView for sample code currently!
        String description = intent.getStringExtra(MyListAdapter.EXTRA_DESCRIPTION);

        if (!TextUtils.isEmpty(sample)) {
            description += "\n\n" + sample;
        }
        description = description.replace("*", "\n");

        descripTextView.setText(description);

        String images = intent.getStringExtra(MyListAdapter.EXTRA_IMAGES);
        mHandler = new Handler();

        //Create a placeholder regarding image's color while the image loads
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);
        Log.d(TAG, "onCreate: " + hex);
        gradientDrawable.setSize(500, 500);
        showImages(detailImageView, gradientDrawable, images);

    }

    private void updateDatabase(String rowId, String column) {
        if (mDBHelper == null) {
            mDBHelper = new MySQLiteHelper(this);
        }
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(column, 1);
        try {
            int update = database.update(
                    MySQLiteHelper.TABLE_NAME,
                    values,
                    MySQLiteHelper.COLUMN_ID + " = ?",
                    new String[]{rowId});
            Log.d(TAG, ": Successfully updated with " + update);
        } catch (SQLException e) {
            Log.e(TAG, "Error updating database ", e);
        }finally {
            database.close();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpButton();
    }

    private void setUpButton() {
        final Button buttonGotIt = findViewById(R.id.button_got_it);
        if (isHidden()) {
            buttonGotIt.setVisibility(View.GONE);
            return;
        }
        buttonGotIt.post(new Runnable() {
            @Override
            public void run() {
                // Get the center for clipping circle
                int cx = buttonGotIt.getWidth() / 2;
                int cy = buttonGotIt.getHeight() / 2;

                // Get the initial radius for clipping circle
                float radius = (float) Math.hypot(cx, cy);

                // Create the animation, the final radius is zero
                final Animator anim =
                        ViewAnimationUtils.createCircularReveal(buttonGotIt, cx, cy, radius, 0);

                // When animation is done, make the view invisible.
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        buttonGotIt.setVisibility(View.GONE);
                    }
                });
                buttonGotIt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        anim.start();
                        updateDatabase(mId, MySQLiteHelper.COLUMN_GET);
                    }
                });

            }
        });


    }

    private boolean isHidden() {
        if (mDBHelper == null) {
            mDBHelper = new MySQLiteHelper(this);
        }
        int get = 0;
        try( SQLiteDatabase readableDatabase = mDBHelper.getReadableDatabase();
             Cursor cursor = readableDatabase.query(MySQLiteHelper.TABLE_NAME,
                     new String[]{MySQLiteHelper.COLUMN_GET},
                     MySQLiteHelper.COLUMN_ID + " = ?",
                     new String[]{mId},
                     null, null, null)){

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    get = cursor.getInt(0);
                }else{
                    Log.e(TAG, "checkToHide: Cursor is empty");
                }

            }else{
                Log.e(TAG, "checkToHide: Cursor is null");
            }

        }catch (SQLException e){
            Log.e(TAG, "checkToHide: ",e );
        }

        return get == 1;
    }

    private void showImages(final ImageView detailImageView, Drawable placeholder, String images) {
        if (images.contains(",")) {
            mImageArray = images.split(",");
        } else {
            mImageArray = new String[]{images};
        }

        Picasso.with(detailImageView.getContext())
                .load(BASE_URL + mImageArray[mIndex])
                .placeholder(placeholder)
                .error(R.drawable.ic_android_new_programmers)
                .fit()
                .into(detailImageView, new Callback() {

                    @Override
                    public void onSuccess() {

                        if (mImageArray.length <= 1) {
                            return;
                        } else if (mImageArray.length > 5) {
                            mDelayMillis = 1000;
                        }
                        mRunnable = new Runnable() {
                            @Override
                            public void run() {
                                mIndex++;
                                if (mIndex == mImageArray.length) {
                                    mIndex = 0;
                                }
                                Picasso.with(detailImageView.getContext())
                                        .load(BASE_URL + mImageArray[mIndex])
                                        .noPlaceholder()
                                        .fit()
                                        .into(detailImageView, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                mHandler.postDelayed(mRunnable, mDelayMillis);
                                            }

                                            @Override
                                            public void onError() {
                                                Log.d(TAG, "onError: Picasso image loading");
                                            }
                                        });

                            }
                        };
                        mHandler.postDelayed(mRunnable, mDelayMillis);
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "onError: Picasso image loading");
                    }

                });
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }

    }


}
