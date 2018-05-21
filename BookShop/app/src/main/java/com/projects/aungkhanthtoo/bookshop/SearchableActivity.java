package com.projects.aungkhanthtoo.bookshop;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.aungkhanthtoo.bookshop.data.ResultSearch;
import com.projects.aungkhanthtoo.bookshop.data.SearchAdapter;
import com.projects.aungkhanthtoo.bookshop.data.SearchResponse;
import com.projects.aungkhanthtoo.bookshop.network.RetrofitHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 2/21/2018.
 */

public class SearchableActivity extends AppCompatActivity {
    private static final String TAG = "SearchableActivity";
    private List<ResultSearch> results;
    private RetrofitHelper mRetrofit;
    private SearchAdapter mAdapter;
    private AutoCompleteTextView mSearchTextView;
    private static final String[] nameHolders = {"Android", "Hacking", "Machine Learning"};
    private static final String[] picHolders = {"115bd0e534de00b776918bcd36026cb5d298de1d.jpg", "creative2.jpg", "education15.jpg"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        mSearchTextView = findViewById(R.id.auto_complete_search);
        mSearchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handle = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Toast.makeText(SearchableActivity.this, "Click", Toast.LENGTH_SHORT).show();
                    doMySearch(mSearchTextView.getText().toString());
                    handle = true;
                }
                return handle;
            }
        });
        results = new ArrayList<>();
        mAdapter = new SearchAdapter(this, results);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(mAdapter);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchTextView.setText(query);
            mSearchTextView.setSelection(query.length());
            Log.d(TAG, "handleIntent: " + query);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {
        if (TextUtils.isEmpty(query)) {
           return;
        }
        mRetrofit = new RetrofitHelper();
        if (!results.isEmpty()) {
            results.clear();
        }
        Call<SearchResponse> call = mRetrofit.getSearchBooks(query);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful()) {
                    SearchResponse body = response.body();
                    if (body.getResult() == null) {
                        findViewById(R.id.empty).setVisibility(View.VISIBLE);
                        return;
                    }
                    findViewById(R.id.empty).setVisibility(View.GONE);
                    for (ResultSearch re :
                            body.getResult()) {
                        Log.d(TAG, "onResponse: "+re.getAuthorName());
                    }
                    results.addAll(body.getResult());
                    mAdapter.notifyDataSetChanged();
                }else{
                    Log.e(TAG, "Server fail");
                }
            }



            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ");
            }
        });

    }


    private SimpleAdapter getSimpleAdapter() {
        List<HashMap<String, String>> list = new ArrayList<>();
        for (int i = 0; i < nameHolders.length; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("title", nameHolders[i]);
            Log.d(TAG, "doMySearch: " + nameHolders[i]);
            list.add(map);
        }
        String[] from = {"title"};
        int[] to = {android.R.id.text1};

        return new SimpleAdapter(this, list, android.R.layout.simple_list_item_1, from, to);

    }
}
