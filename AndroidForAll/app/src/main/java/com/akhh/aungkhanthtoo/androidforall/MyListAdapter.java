package com.akhh.aungkhanthtoo.androidforall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akhh.aungkhanthtoo.androidforall.data.Vocab;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

/**
 * Created by Lenovo on 1/27/2018.
 */

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyViewHolder> {

    private Context mContext;

    private static final String TAG = "MyListAdapter";

    static final String EXTRA_COLOR_HEX= "MyListAdapter.EXTRA_COLOR_ID";
    static final String EXTRA_TITLE = "MyListAdapter.EXTRA_TITLE";
    static final String EXTRA_DESCRIPTION = "MyListAdapter.EXTRA_DESCRIPTION";
    static final String EXTRA_SAMPLE = "MyListAdapter.EXTRA_SAMPLE";
    static final String EXTRA_IMAGES = "MyListAdapter.EXTRA_IMAGES";
    static final String EXTRA_ID = "MyListAdapter.EXTRA_ID";

    private List<Vocab> mList;

    MyListAdapter(List<Vocab> mList, Context context) {
        this.mList = mList;
        mContext = context;
    }

    @Override
    public MyListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view, mContext);
    }


    @Override
    public void onBindViewHolder(final MyListAdapter.MyViewHolder holder, final int position) {

        final Vocab current = mList.get(position);
        holder.mTextView.setText(current.getTitle());
        Picasso.with(mContext)
                .load(DetailActivity.BASE_URL + current.getThumb() )
                .noFade()
                .into(holder.mImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: holder.getAdapterPosition() " + holder.getAdapterPosition());
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(EXTRA_COLOR_HEX, current.getColor());
                intent.putExtra(EXTRA_IMAGES, current.getImages());
                intent.putExtra(EXTRA_DESCRIPTION, current.getDescription());
                intent.putExtra(EXTRA_SAMPLE, current.getSample());
                intent.putExtra(EXTRA_TITLE, current.getTitle());
                intent.putExtra(EXTRA_ID, String.valueOf(holder.getAdapterPosition()+1));
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(((Activity) mContext),
                        new Pair<View, String>(holder.mTextView, DetailActivity.VIEW_NAME_HEADER_TITLE),
                        new Pair<View, String>(holder.mImageView, DetailActivity.VIEW_NAME_IMAGE));
                mContext.startActivity(intent, activityOptions.toBundle());
            }
        });

    }

    /**
     * Returns the total number of items in the data set held by the mAdapter.
     *
     * @return The total number of items in this mAdapter.
     */
    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;
        private ImageView mImageView;
        Typeface mTypeface;

        MyViewHolder(View itemView, Context context) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.textView_list_title);
            mImageView = itemView.findViewById(R.id.list_image);
            mTypeface = Typeface.createFromAsset(context.getAssets(), "roboto_black.ttf");
            mTextView.setTypeface(mTypeface);
        }
    }

    private int getIntInRange(int max, int min) {
        Random random = new Random();
        return random.nextInt(max + min + 1) + min;
    }

    private int getRandomHSVColor() {
        // Generate a random hue value between 0 and 360
        int hue = new Random().nextInt(361);

        // Make the color depth full
        float saturation = 1.0f;
        // Make a full bright color
        float value = 1.0f;
        // Avoid the transparency
        int alpha = 255;
        //Finally generate the color

        return Color.HSVToColor(alpha, new float[]{hue, saturation, value});

    }
}
