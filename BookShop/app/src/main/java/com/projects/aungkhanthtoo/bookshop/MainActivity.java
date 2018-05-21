package com.projects.aungkhanthtoo.bookshop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.aungkhanthtoo.bookshop.data.Author;
import com.projects.aungkhanthtoo.bookshop.data.AuthorsResponse;
import com.projects.aungkhanthtoo.bookshop.data.BooksResponse;
import com.projects.aungkhanthtoo.bookshop.data.Genre;
import com.projects.aungkhanthtoo.bookshop.data.GenresResponse;
import com.projects.aungkhanthtoo.bookshop.data.Header;
import com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter;
import com.projects.aungkhanthtoo.bookshop.data.MyOtherAdapter;
import com.projects.aungkhanthtoo.bookshop.data.Publisher;
import com.projects.aungkhanthtoo.bookshop.data.PublishersResponse;
import com.projects.aungkhanthtoo.bookshop.data.Result;
import com.projects.aungkhanthtoo.bookshop.network.RetrofitHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOGIN = 695;
    private static final int REQUEST_BOOK_CREATE = 260;
    public static final int REQUEST_BOOK_EDIT = 807;

    private TextView mTabOneTextView;
    private TextView mTabTwoTextView;
    private MyBookAdapter mAdapter;
    private List<Result> mBookList;
    private RetrofitHelper mRetrofit;
    private List mOtherList;
    boolean hasLogin;
    private EditText mEditText;
    private View mProgressView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressView = findViewById(R.id.main_progress);
        int pre = getSharedPreferences("login", MODE_PRIVATE).getInt("login", 0);
        hasLogin = pre == 1;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        recyclerView = findViewById(R.id.recycler);
        mTabOneTextView = findViewById(R.id.tvTabOne);
        mTabTwoTextView = findViewById(R.id.tvTabTwo);
        mTabOneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set(0);
                showBooks(recyclerView);
            }
        });
        mTabTwoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set(1);
                showOthers(recyclerView);
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchRequested();
            }
        });
        mOtherList = new ArrayList<>(0);
        mBookList = new ArrayList<>(0);
        mAdapter = new MyBookAdapter(mBookList, MainActivity.this, hasLogin);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0)
                    fab.hide();
                else
                    fab.show();
            }
        });
        showBooks(recyclerView);
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

    @Override
    protected void onStart() {
        super.onStart();
        int pre = getSharedPreferences("login", MODE_PRIVATE).getInt("login", 0);
        hasLogin = pre == 1;
        if (hasLogin) {
            invalidateOptionsMenu();
        }
    }

    private void showBooks(final RecyclerView recyclerView) {
        if (mRetrofit == null) {
            mRetrofit = new RetrofitHelper();
        }
        if (mBookList.isEmpty()) {
            Call<BooksResponse> call = mRetrofit.getBooks();
            call.enqueue(new Callback<BooksResponse>() {
                @Override
                public void onResponse(Call<BooksResponse> call, final Response<BooksResponse> response) {
                    final BooksResponse body = response.body();
                    if (body != null) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                final List<Result> reverse = getLatestFirst(body.getResult());
                                mBookList.addAll(reverse);
                                recyclerView.setBackgroundResource(R.color.recycler_background);
                                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                                recyclerView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                                runOnUiThread(() -> showProgress(false));
                            }
                        });

                    }
                }

                @Override
                public void onFailure(Call<BooksResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: Books");
                }
            });
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
            recyclerView.setBackgroundResource(R.color.recycler_background);
            recyclerView.setAdapter(mAdapter);
        }

    }

    private void showOthers(final RecyclerView recyclerView) {
        if (mRetrofit == null) {
            mRetrofit = new RetrofitHelper();
        }
        if (mOtherList.isEmpty()) {
            showProgress(true);
            Call<GenresResponse> genres = mRetrofit.getGenres();
            genres.enqueue(new Callback<GenresResponse>() {
                @Override
                public void onResponse(Call<GenresResponse> call, Response<GenresResponse> response) {
                    if (response.isSuccessful()) {
                        GenresResponse body = response.body();
                        final List<Genre> result = body.getResult();
                        Collections.sort(result);
                        mOtherList.add(new Header(0, "Genres"));
                        mOtherList.addAll(result);
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... voids) {
                                getFinalList(recyclerView);
                                return null;
                            }

                        }.execute();
                    } else {
                        Log.d(TAG, "server contact failed");
                    }
                }

                @Override
                public void onFailure(Call<GenresResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: Genres", t);
                }
            });
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            MyOtherAdapter adapter = new MyOtherAdapter(MainActivity.this, mOtherList, hasLogin);
            recyclerView.setAdapter(adapter);
        }
    }

    private void getFinalList(final RecyclerView recyclerView) {
        getToAuthor();
        if (mRetrofit == null) {
            mRetrofit = new RetrofitHelper();
        }
        Call<PublishersResponse> publishers = mRetrofit.getPublishers();
        publishers.enqueue(new Callback<PublishersResponse>() {
            @Override
            public void onResponse(Call<PublishersResponse> call, Response<PublishersResponse> response) {
                if (response.isSuccessful()) {
                    PublishersResponse body = response.body();
                    List<Publisher> result = body.getResult();
                    Collections.sort(result);
                    mOtherList.add(new Header(mOtherList.size(), "Publishers"));
                    mOtherList.addAll(result);
                    runOnUiThread(() -> {
                        showProgress(false);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        MyOtherAdapter adapter = new MyOtherAdapter(MainActivity.this, mOtherList, hasLogin);
                        recyclerView.setAdapter(adapter);
                    });
                } else {
                    Log.d(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<PublishersResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Publishers", t);
            }
        });

    }

    private void getToAuthor() {
        if (mRetrofit == null) {
            mRetrofit = new RetrofitHelper();
        }
        Call<AuthorsResponse> authors = mRetrofit.getAuthors();
        authors.enqueue(new Callback<AuthorsResponse>() {
            @Override
            public void onResponse(Call<AuthorsResponse> call, Response<AuthorsResponse> response) {
                if (response.isSuccessful()) {
                    AuthorsResponse body = response.body();
                    List<Author> result = body.getResult();
                    Collections.sort(result);
                    mOtherList.add(new Header(mOtherList.size(), "Authors"));
                    mOtherList.addAll(result);
                } else {
                    Log.d(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<AuthorsResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Authors", t);
            }
        });
    }

    private List<Result> getLatestFirst(List<Result> input) {
        int i = 0;
        int j = input.size() - 1;
        while (i < j) {
            Result temp = input.get(i);
            input.set(i, input.get(j));
            input.set(j, temp);
            i++;
            j--;
        }
        return input;
    }

    private void set(int tab) {
        if (tab == 0) {
            mTabOneTextView.setBackgroundResource(R.drawable.background_left_pressed);
            mTabOneTextView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

            mTabTwoTextView.setBackgroundResource(R.drawable.background_right);
            mTabTwoTextView.setTextColor(Color.WHITE);
        } else {
            mTabTwoTextView.setBackgroundResource(R.drawable.background_right_pressed);
            mTabTwoTextView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

            mTabOneTextView.setBackgroundResource(R.drawable.background_left);
            mTabOneTextView.setTextColor(Color.WHITE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (hasLogin) {
            menu.setGroupVisible(R.id.action_admin_group, true);
            menu.findItem(R.id.action_login).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        } else if (id == R.id.action_create) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setSingleChoiceItems(R.array.add_items, 0, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        if (i == 0) {
                            Intent intent = new Intent(this, BookEditorActivity.class);
                            startActivityForResult(intent, REQUEST_BOOK_CREATE);
                            return;
                        }
                        showCreateDialog(i, (dialogInterface1, i1) -> create(i));

                    })
                    .create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void create(int code) {
        hideKeyboard();
        String type;
        final String name = mEditText.getText().toString();
        Call<BooksResponse> call = null;
        switch (code) {
            case 1:
                type = "genre";
                call = mRetrofit.createGenre(name);
                break;
            case 2:
                type = "author";
                call = mRetrofit.createAuthor(name);
                break;
            case 3:
                type = "publisher";
                call = mRetrofit.createPublisher(name);
                break;
            default:
                type = "";
        }
        call.enqueue(new Callback<BooksResponse>() {
            @Override
            public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                if (response.isSuccessful()) {
                    BooksResponse body = response.body();
                    if (body.getStatus() == 1) {
                        Toast.makeText(MainActivity.this, "New " + type + " is created.", Toast.LENGTH_SHORT).show();
                        recreate();
                    } else {
                        Toast.makeText(MainActivity.this, "Adding a " + type + " is unsuccessful!", Toast.LENGTH_SHORT).show();
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

    private void showCreateDialog(int position, DialogInterface.OnClickListener listener) {
        String title;
        switch (position) {
            case 1:
                title = "Create a genre";
                break;
            case 2:
                title = "Create an author";
                break;
            case 3:
                title = "Create a publisher";
                break;
            default:
                title = "";
        }
        View view = LayoutInflater.from(this).inflate(R.layout.custom_view_dialog, null
                , false);
        mEditText = view.findViewById(R.id.et_name_dialog);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setCancelable(false)
                .setView(view)
                .setPositiveButton("Ok", listener)
                .setNegativeButton("Cancel", (dialogInterface, i) -> hideKeyboard())
                .create().show();
        showKeyboard();
    }

    private void showKeyboard() {
        InputMethodManager inputMananger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMananger.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideKeyboard() {
        InputMethodManager inputMananger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMananger.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }


    private void logout() {
        Call<BooksResponse> logout = mRetrofit.logout();
        logout.enqueue(new Callback<BooksResponse>() {
            @Override
            public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 1) {
                        Toast.makeText(MainActivity.this, "Successfully logout.", Toast.LENGTH_SHORT).show();
                        getSharedPreferences("login", MODE_PRIVATE).edit().putInt("login", 0).apply();
                        recreate();
                    }
                } else {
                    Log.e(TAG, "Server fails!");
                }
            }

            @Override
            public void onFailure(Call<BooksResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    private void showDialog(String title, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(R.layout.custom_view_dialog)
                .setPositiveButton("Ok", listener)
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOGIN || requestCode == REQUEST_BOOK_CREATE || requestCode == REQUEST_BOOK_EDIT) {
                recreate();
            }
        }
    }
}
