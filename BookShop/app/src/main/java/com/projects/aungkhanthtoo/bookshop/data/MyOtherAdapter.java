package com.projects.aungkhanthtoo.bookshop.data;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.aungkhanthtoo.bookshop.CatalogActivity;
import com.projects.aungkhanthtoo.bookshop.R;
import com.projects.aungkhanthtoo.bookshop.network.RetrofitHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 2/20/2018.
 */

public class MyOtherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MyOtherAdapter";

    private static final int TYPE_HEADER = 50;
    private static final int TYPE_ITEM = 100;
    private final Context mContext;

    public static final String ACTION_GENRE = "com.projects.aungkhanthtoo.bookshop.GENRE";
    public static final String ACTION_AUTHOR= "com.projects.aungkhanthtoo.bookshop.AUTHOR";
    private static final String ACTION_PUBLISHER = "com.projects.aungkhanthtoo.bookshop.PUBLISHER";
    public static final String EXTRA_ID = "com.projects.aungkhanthtoo.bookshop.extraId";
    public static final String EXTRA_NAME = "com.projects.aungkhanthtoo.bookshop.extraName";
    private List mDataSet;
    private boolean hasLogin;
    private ViewGroup mParent;
    EditText mEditText;
    private RetrofitHelper mRetrofit;

    public MyOtherAdapter(Context context, List mDataSet) {
       this(context, mDataSet, false);
    }

    public MyOtherAdapter(Context context, List mDataSet, boolean login) {
        this.mDataSet = mDataSet;
        mContext = context;
        hasLogin = login;
        mRetrofit = new RetrofitHelper();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mParent == null) {
            mParent = parent;
        }
        if (viewType == TYPE_HEADER) {
            return new VHHeader(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false));
        }else{
            return new VHItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_other, parent, false));
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object item = mDataSet.get(position);

        if (item instanceof Header) {
            Header current = ((Header) item);
            Log.d(TAG, "onResponse Author: " + current.getName() + " " + current.getPosition());
            ((VHHeader) holder).mTextView.setText(current.getName());
        } else if (item instanceof Genre) {
            Genre current = ((Genre) item);
            Log.d(TAG, "onResponse Author: " + current.getGenreName());
            TextView textView = ((VHItem) holder).mTextView;
            textView.setText(current.getGenreName());
            textView.setOnClickListener(view -> onClick(current, position));
        } else if (item instanceof Author) {
            Author current = ((Author) item);
            Log.d(TAG, "onResponse Author: " + current.getAuthorName());
            TextView textView = ((VHItem) holder).mTextView;
            textView.setText(current.getAuthorName());
            textView.setOnClickListener(view -> onClick(current, position));
        } else {
            Publisher current = ((Publisher) item);
            Log.d(TAG, "onResponse Publisher: " + current.getPname());
            TextView textView = ((VHItem) holder).mTextView;
            textView.setText(current.getPname());
            textView.setOnClickListener(view -> onClick(current, position));
        }
    }

    private <T> void onClick(T item, int position){
        if (hasLogin) {
            showUpdateDialog(item, (dialogInterface, i) -> update(item, position));
            return;
        }
        sendIntent(item);
    }

    private <T> void update(T item, int position) {
        String msg;
        final String updatedName = mEditText.getText().toString();
        Call<BooksResponse> call;
        if (item instanceof Genre) {
           msg = "Genre";
           call = mRetrofit.editGenre(((Genre) item).getId(), updatedName);
        }else if(item instanceof Author){
          msg = "Author";
            call = mRetrofit.editAuthor(((Author) item).getId(), updatedName);
        }else{
           msg = "Publisher";
            call = mRetrofit.editPublisher(((Publisher) item).getId(), updatedName);
        }
        call.enqueue(new Callback<BooksResponse>() {
            @Override
            public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                if (response.isSuccessful()) {
                    BooksResponse body = response.body();
                    if (body.getStatus() == 1) {
                        Toast.makeText(mContext, msg + " is successfully updated.", Toast.LENGTH_SHORT).show();
                        if(item instanceof Genre){
                            ((Genre) item).setGenreName(updatedName);
                            notifyItemChanged(position);
                        }
                    }else{
                        Toast.makeText(mContext, "Updating fails!", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Log.e(TAG, "Server contact fails");
                }
            }

            @Override
            public void onFailure(Call<BooksResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });

        InputMethodManager inputMananger = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMananger.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private <T> void sendIntent(T item){
        String action;
        int id;
        String name;
        if (item instanceof Genre) {
            action = ACTION_GENRE;
            id = ((Genre) item).getId();
            name = ((Genre) item).getGenreName();
        }else if(item instanceof Author){
            action = ACTION_AUTHOR;
            id = ((Author) item).getId();
            name = ((Author) item).getAuthorName();
        }else{
            action = ACTION_PUBLISHER;
            id = ((Publisher) item).getId();
            name = ((Publisher) item).getPname();
        }
        Intent intent = new Intent(mContext, CatalogActivity.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_NAME, name);
        mContext.startActivity(intent);
    }

    private <T> void showUpdateDialog(T item, DialogInterface.OnClickListener listener) {
        String title;
        String itemName;
        if (item instanceof Genre) {
            title = "Update Genre";
            itemName = ((Genre) item).getGenreName();
        }else if(item instanceof Author){
            title = "Update Author";
            itemName = ((Author) item).getAuthorName();
        }else{
            title = "Update Publisher";
            itemName = ((Publisher) item).getPname();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_view_dialog, null
    , false);
        mEditText = view.findViewById(R.id.et_name_dialog);
        mEditText.setText(itemName);
        mEditText.setSelectAllOnFocus(true);
        mEditText.requestFocus();
        InputMethodManager inputMananger = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMananger.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setCancelable(false)
                .setView(view)
                .setPositiveButton("Ok", listener)
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    inputMananger.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                })
                .create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + mDataSet.size());
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = mDataSet.get(position);
        if (item instanceof Header) {
         return TYPE_HEADER;
        }

        return TYPE_ITEM;
    }

    static class VHItem extends RecyclerView.ViewHolder{
        private TextView mTextView;

        public VHItem(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_list_item);
        }
    }

    static class VHHeader extends RecyclerView.ViewHolder{
        private TextView mTextView;

        public VHHeader(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_list_header);
        }
    }
}
