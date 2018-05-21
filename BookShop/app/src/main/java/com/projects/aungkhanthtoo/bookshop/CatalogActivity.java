package com.projects.aungkhanthtoo.bookshop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.projects.aungkhanthtoo.bookshop.data.BooksResponse;
import com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter;
import com.projects.aungkhanthtoo.bookshop.data.MyOtherAdapter;
import com.projects.aungkhanthtoo.bookshop.data.Result;
import com.projects.aungkhanthtoo.bookshop.network.RetrofitHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogActivity extends AppCompatActivity {
    RetrofitHelper mRetrofit;
    private static final String TAG = "CatalogActivity";
    private List<Result> mBookList;
    private MyBookAdapter mAdapter;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean hasLogin = getSharedPreferences("login", MODE_PRIVATE).getInt("login", 0) == 1;
        setContentView(R.layout.activity_catalog);
        mProgressView = findViewById(R.id.catalog_progress);
        mRetrofit = new RetrofitHelper();
        RecyclerView recyclerView = findViewById(R.id.recycler);
        mBookList = new ArrayList<>(0);
        mAdapter = new MyBookAdapter(mBookList, this, hasLogin);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(mAdapter);
        handle(getIntent());
    }

    private void showProgress(final boolean show) {
        // If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

    }
    private void handle(Intent intent) {
        getSupportActionBar().setTitle(intent.getStringExtra(MyOtherAdapter.EXTRA_NAME));
        showBooks(intent);
    }

    private void showBooks(Intent intent) {
        final String action = intent.getAction();
        final int id = intent.getIntExtra(MyOtherAdapter.EXTRA_ID, 0);
        Call<BooksResponse> call;
        if (action.equals(MyOtherAdapter.ACTION_GENRE)) {
            call = mRetrofit.getBooksByGenre(id);
        }else if(action.equals(MyOtherAdapter.ACTION_AUTHOR)){
            call = mRetrofit.getBooksByAuthor(id);
        }else{
            call = mRetrofit.getBooksByPublisher(id);
        }

        call.enqueue(new Callback<BooksResponse>() {
            @Override
            public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                if (response.isSuccessful()) {
                    showProgress(false);
                    List<Result> results = response.body().getResult();
                    if (results != null) {
                        mBookList.addAll(results);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(CatalogActivity.this, "No book is available currently.", Toast.LENGTH_SHORT).show();
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

    }
}
