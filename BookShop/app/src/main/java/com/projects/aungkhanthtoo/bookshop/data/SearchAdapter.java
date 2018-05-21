package com.projects.aungkhanthtoo.bookshop.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.projects.aungkhanthtoo.bookshop.DetailActivity;
import com.projects.aungkhanthtoo.bookshop.R;

import java.util.List;

/**
 * Created by Lenovo on 2/21/2018.
 */

public class SearchAdapter extends ArrayAdapter<ResultSearch> {
    private final Context mContext;
    private List<ResultSearch> mDataSet;
    public static final String IMAGE_URL_PREFIX = "http://student.newwestgroup.org/bookstore/public/sites/upload/image/";

    public SearchAdapter(@NonNull Context context, List<ResultSearch> data) {
       super(context, 0, data);
        mDataSet = data;
        mContext = context;
    }

    static class ViewHolder{
        private ImageView mImage;
        private TextView mText;
        private View itemView;

        ViewHolder(View itemView){
            mImage = itemView.findViewById(R.id.iv_search_book);
            mText = itemView.findViewById(R.id.tv_search_book);
            this.itemView = itemView;
        }

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = ((ViewHolder) convertView.getTag());
        }

        final ResultSearch item = mDataSet.get(position);
        holder.mText.setText(item.getName());
        Glide.with(mContext)
                .load(IMAGE_URL_PREFIX + item.getImage())
                .into(holder.mImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendIntent(mContext, item);
            }
        });

        return convertView;
    }

    private void sendIntent(Context context, ResultSearch current){
        Intent intent = new Intent(context, DetailActivity.class);
        Bundle bundle = new Bundle(9);
        bundle.putString(MyBookAdapter.EXTRA_AUTHOR, current.getAuthorName());
        bundle.putString(MyBookAdapter.EXTRA_DESCRIPT, current.getDescription());
        bundle.putString(MyBookAdapter.EXTRA_GENRE, current.getGenreName());
        bundle.putString(MyBookAdapter.EXTRA_TITLE, current.getName());
        bundle.putString(MyBookAdapter.EXTRA_PRICE,String.valueOf(current.getPrice()));
        bundle.putString(MyBookAdapter.EXTRA_PUBDATE, current.getPublishingDate() == null ? "-" : current.getPublishingDate());
        bundle.putString(MyBookAdapter.EXTRA_PUBLISHER, current.getPname());
        bundle.putString(MyBookAdapter.EXTRA_IMAGE, IMAGE_URL_PREFIX+current.getImage());
        bundle.putString(MyBookAdapter.EXTRA_PDF, current.getSavePdf());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}


