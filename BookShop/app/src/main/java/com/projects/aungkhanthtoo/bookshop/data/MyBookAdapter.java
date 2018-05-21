package com.projects.aungkhanthtoo.bookshop.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.projects.aungkhanthtoo.bookshop.BookEditorActivity;
import com.projects.aungkhanthtoo.bookshop.DetailActivity;
import com.projects.aungkhanthtoo.bookshop.MainActivity;
import com.projects.aungkhanthtoo.bookshop.R;
import com.projects.aungkhanthtoo.bookshop.network.RetrofitHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 2/18/2018.
 */

public class MyBookAdapter extends RecyclerView.Adapter<MyBookAdapter.MyBookHolder> {

    private static final String TAG = "MyBookAdapter";

    public static final String EXTRA_TITLE = "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraTitle";
    public static final String EXTRA_AUTHOR = "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraAuthor";
    public static final String EXTRA_AUTHOR_ID = "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraAuthorId";
    public static final String EXTRA_DESCRIPT = "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraDescript";
    public static final String EXTRA_PRICE = "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraPrice";
    public static final String EXTRA_PUBDATE = "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraPubDate";
    public static final String EXTRA_PUBLISHER = "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraPublisher";
    public static final String EXTRA_PUBLISHER_ID = "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraPublisherId";
    public static final String EXTRA_GENRE = "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraGenre";
    public static final String EXTRA_GENRE_ID = "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraGenreId";
    public static final String EXTRA_IMAGE = "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraImage";
    public static final String EXTRA_PDF = "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraPdf";
    public static final String EXTRA_ID = "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraId";
    public static final String EXTRA_NO = "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraNo";
    public static final String EXTRA_EDITION =  "com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter.extraEdition";;

    private final boolean mHasLogin;
    private List<Result> mDataSet;
    private Context mContext;

    public MyBookAdapter(List<Result> data, Context context, boolean login) {
        this.mDataSet = data;
        this.mContext = context;
        mHasLogin = login;
    }

    static class MyBookHolder extends RecyclerView.ViewHolder {

        private final boolean mHasLogin;
        private TextView mDeleteTextView;
        private ImageView mBookImage;
        private List<Result> mData;

        public MyBookHolder(final View itemView, List<Result> data, boolean login) {
            super(itemView);
            mData = data;
            mHasLogin = login;
            mBookImage = itemView.findViewById(R.id.image_grid_book);
            mDeleteTextView = itemView.findViewById(R.id.tv_grid_book_delete);
            itemView.setOnClickListener(view -> sendIntent(itemView.getContext()));
        }

        private void sendIntent(Context context) {
            final Intent intent;
            Bundle bundle = new Bundle();
            Result current = mData.get(this.getAdapterPosition());
            bundle.putString(EXTRA_DESCRIPT, current.getDescription());
            bundle.putString(EXTRA_TITLE, current.getName());
            bundle.putString(EXTRA_PRICE, String.valueOf(current.getPrice()));
            bundle.putString(EXTRA_PUBDATE, current.getPublishingDate() == null ? "-" : current.getPublishingDate().toString());
            bundle.putString(EXTRA_IMAGE, current.getImage().startsWith("http") ? current.getImage() : SearchAdapter.IMAGE_URL_PREFIX + current.getImage());
            bundle.putString(EXTRA_PDF, current.getSavePdf());
            if (mHasLogin) {
                bundle.putInt(EXTRA_AUTHOR_ID, current.getAuthorId());
                bundle.putInt(EXTRA_GENRE_ID, current.getGenreId());
                bundle.putInt(EXTRA_PUBLISHER_ID, current.getPublisherId());
                bundle.putString(EXTRA_ID, String.valueOf(current.getId()));
                bundle.putString(EXTRA_NO, current.getCodenumber());
                bundle.putString(EXTRA_EDITION, String.valueOf(current.getEdition()));
                intent = new Intent(context, BookEditorActivity.class);
                intent.putExtras(bundle);
                ((Activity) context).startActivityForResult(intent, MainActivity.REQUEST_BOOK_EDIT);
            }else{
                bundle.putString(EXTRA_AUTHOR, current.getAuthor().getAuthorName());
                bundle.putString(EXTRA_GENRE, current.getGenre().getGenreName());
                bundle.putString(EXTRA_PUBLISHER, current.getPublisher().getPname());
                intent = new Intent(context, DetailActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }

        }
    }

    private void deleteBook(MyBookHolder holder) {

        new AlertDialog.Builder(mContext)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Ok", (dialogInterface, i) -> verifyDelete(holder))
                .setNegativeButton("Cancel", null)
                .create()
                .show();

    }

    private void verifyDelete(MyBookHolder holder) {
        final RetrofitHelper retrofit = new RetrofitHelper();
        Call<BooksResponse> call = retrofit.deleteBook(mDataSet.get(holder.getAdapterPosition()).getId());
        call.enqueue(new Callback<BooksResponse>() {
            @Override
            public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                if (response.isSuccessful()) {
                    BooksResponse body = response.body();
                    if (body.getStatus() == 1) {
                        Toast.makeText(mContext, "Book deleted", Toast.LENGTH_SHORT).show();
                        mDataSet.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    } else {
                        Toast.makeText(mContext, "Deleting fails!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Server contact fails!");
                }
            }

            @Override
            public void onFailure(Call<BooksResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    @Override
    public MyBookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_book, parent, false);
        return new MyBookHolder(itemView, mDataSet, mHasLogin);
    }

    @Override
    public void onBindViewHolder(MyBookHolder holder, int position) {
        Result current = mDataSet.get(position);
        if (mHasLogin) {
            holder.mDeleteTextView.setVisibility(View.VISIBLE);
            holder.mDeleteTextView.setOnClickListener(view -> deleteBook(holder));
        }
        Glide.with(mContext)
                .load(current.getImage().startsWith("http") ? current.getImage() : SearchAdapter.IMAGE_URL_PREFIX + current.getImage())
                .into(holder.mBookImage);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


}
