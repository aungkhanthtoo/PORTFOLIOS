package com.akhh.aungkhanthtoo.androidforall;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.akhh.aungkhanthtoo.androidforall.data.MySQLiteHelper;
import com.akhh.aungkhanthtoo.androidforall.data.Vocab;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FundamentalActivity extends Activity {

    private static final String TAG = "FundamentalActivity";
    static final String QUIZ_PREFERENCE = "quiz";

    private DatabaseReference mListRef;
    private List<Vocab> mList;
    MyListAdapter mAdapter;
    private ProgressBar mProgressBar;
    private MySQLiteHelper mDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        checkUserSettings();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        mListRef = mRootRef.child("fundamentals");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, AuthUiSignInActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_fundamental);
        mProgressBar = findViewById(R.id.loading_spinner);
        if (getActionBar() != null) {
            getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryBlue)));
        }
        mList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new MyListAdapter(mList, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);

    }// Generate keyhash for facebook log in
    //  try{
//            PackageInfo packageInfo = getPackageManager().getPackageInfo(
//                    "com.akhh.aungkhanthtoo.androidforall",
//                    PackageManager.GET_SIGNATURES
//            );
//            for(Signature signature : packageInfo.signatures){
//                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
//                messageDigest.update(signature.toByteArray());
//                Log.d("KEYHASH", "onCreate: "+ Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
//            }
//
//        }catch (PackageManager.NameNotFoundException e){
//            Log.e(TAG, "onCreate: ", e);
//
//        }catch(NoSuchAlgorithmException e){
//            Log.e(TAG, "onCreate: ", e);
//        }

    private void checkUserSettings() {
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isOfflineMode = preferences.getBoolean(getResources().getString(R.string.offline_pref_key), false);

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(isOfflineMode);
        }catch (DatabaseException e){
            Log.e(TAG, "checkUserSettings: ", e );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mList.isEmpty()) {
            mListRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    showVocabs(dataSnapshot);
                    mDBHelper = new MySQLiteHelper(FundamentalActivity.this, mList);
                    SQLiteDatabase readableDatabase = mDBHelper.getReadableDatabase();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting failed, log a message
                    Log.w(TAG, "loading:onCancelled", databaseError.toException());
                    // ...
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        checkForQuiz();
    }

    private void showVocabs(DataSnapshot dataSnapshot) {
        if (!mList.isEmpty()) {
            mList.clear();
        }
        for (DataSnapshot data :
                dataSnapshot.getChildren()) {
            Vocab vocab = data.getValue(Vocab.class);
            mList.add(vocab);
        }
        mAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // get Search MenuItem
        MenuItem item = menu.findItem(R.id.search);
        // get SearchView
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            DatabaseReference databaseReference = mListRef.orderByChild("title").getRef();
            @Override
            public boolean onQueryTextSubmit(String s) {
                View currentFocus = FundamentalActivity.this.getCurrentFocus();
                if (currentFocus != null) {
                    InputMethodManager systemService = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    systemService.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s)) {
                   s = s.toLowerCase();
                }

                mListRef.orderByKey().startAt(s).endAt(s+"\uf8ff").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        showVocabs(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(FundamentalActivity.this, databaseError.getDetails(), Toast.LENGTH_LONG).show();
                    }
                });

                return true;
            }
        });

        // Associate SearchView with the searchable config
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        new ComponentName(getPackageName(), FundamentalActivity.class.getName());
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        // When user submits query, SearchView will fire with ACTION_SEARCH

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void checkForQuiz() {
        mDBHelper = new MySQLiteHelper(this);
        SQLiteDatabase readableDatabase = mDBHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = readableDatabase.query(MySQLiteHelper.TABLE_NAME, null,
                    MySQLiteHelper.COLUMN_GET + " = ? AND " + MySQLiteHelper.COLUMN_TEST + " = ?",
                    new String[]{"1", "0"},
                    null, null, null,
                    "1");
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    final String question = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_QUESTION));
                    if (question.equals("Coming soon.")) {
                        getSharedPreferences(QUIZ_PREFERENCE, MODE_PRIVATE)
                                .edit()
                                .putLong(MySQLiteHelper.COLUMN_ID, 0)
                                .apply();
                        return;
                    }
                    final String title = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TITLE));
                    Log.d(TAG, "checkToTest: "+question);
                    final int answer = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ANSWER));
                    final String explanation = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EXPLANATION));
                    final long id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID));

                    getSharedPreferences(QUIZ_PREFERENCE, MODE_PRIVATE)
                            .edit()
                            .putLong(MySQLiteHelper.COLUMN_ID, id)
                            .putString(MySQLiteHelper.COLUMN_QUESTION, question)
                            .putString(MySQLiteHelper.COLUMN_TITLE, title)
                            .putInt(MySQLiteHelper.COLUMN_ANSWER, answer)
                            .putString(MySQLiteHelper.COLUMN_EXPLANATION, explanation)
                            .apply();
                }else{
                   getSharedPreferences(QUIZ_PREFERENCE, MODE_PRIVATE)
                           .edit()
                           .putLong(MySQLiteHelper.COLUMN_ID, 0)
                           .apply();
                }
            }else{
                Log.e(TAG, "checkToTest: cursor = null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
                readableDatabase.close();
            }
        }

    }

}
