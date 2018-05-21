package com.projects.aungkhanthtoo.bookshop;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter;
import com.projects.aungkhanthtoo.bookshop.network.RetrofitHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    private String[] texts;
    private int mIndex;
    private Handler mHandler;
    private Runnable mRunnable;
    private TextSwitcher mTextSwitcher;
    private String mPdfFile;
    private static final int REQUEST_STORAGE = 10;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mTextSwitcher = findViewById(R.id.textSwitcher);
        TextView textVeiwDescript = findViewById(R.id.textView_description_book);
        TextView textVeiwPrice = findViewById(R.id.textView_price_book);
        TextView textVeiwPubDate = findViewById(R.id.textView_pubdate_book);
        TextView textVeiwPublisher = findViewById(R.id.textView_publisher_book);
        TextView textVeiwGenre = findViewById(R.id.textView_genre_book);
        ImageView imageBook = findViewById(R.id.imageView_detail_book);
        showImage(imageBook);
        texts = new String[]{getData(MyBookAdapter.EXTRA_TITLE), "Written by: " + getData(MyBookAdapter.EXTRA_AUTHOR)};
        textVeiwDescript.setText(getData(MyBookAdapter.EXTRA_DESCRIPT));
        textVeiwPrice.setText(getData(MyBookAdapter.EXTRA_PRICE));
        textVeiwPubDate.setText(getData(MyBookAdapter.EXTRA_PUBDATE));
        textVeiwPublisher.setText(getData(MyBookAdapter.EXTRA_PUBLISHER));
        textVeiwGenre.setText(getData(MyBookAdapter.EXTRA_GENRE));
        textVeiwPrice.setText(getData(MyBookAdapter.EXTRA_PRICE));
        mPdfFile = getData(MyBookAdapter.EXTRA_PDF);
        FloatingActionButton fab = findViewById(R.id.fab);
        if (mPdfFile == null) {
            fab.setVisibility(View.GONE);
        } else {
            mProgress = findViewById(R.id.fab_progress);
            fab.setOnClickListener(view -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        downloadPdf();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(DetailActivity.this, "External Storage is needed to download the PDF file.", Toast.LENGTH_LONG).show();
                        }
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadPdf();
            } else {
                Toast.makeText(this, "Permission was not granted.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

    private void downloadPdf() {
        RetrofitHelper helper = new RetrofitHelper();
        mProgress.setVisibility(View.VISIBLE);

        Call<ResponseBody> call = helper.getPdf("/bookstore/public/sites/upload/pdf/" + mPdfFile);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "server contacted and has file");

                    if (writeResponseBodyToDisk(response.body())) {
                        Toast.makeText(DetailActivity.this, "Successfully Downloaded.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(DetailActivity.this, "Download fail!", Toast.LENGTH_SHORT).show();
                    }
                    mProgress.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: ");
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + mPdfFile);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    private void showImage(ImageView imageView) {
        Glide.with(this)
                .load(getData(MyBookAdapter.EXTRA_IMAGE))
                .into(imageView);
    }

    private String getData(String key) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            return bundle.getString(key);
        }
        throw new IllegalStateException("Bundle is null");
    }

    @Override
    protected void onStart() {
        super.onStart();
        switchTexts(mTextSwitcher);
    }

    private void switchTexts(final TextSwitcher textSwitcher) {
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(DetailActivity.this);
                textView.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
                ));
                textView.setGravity(Gravity.CENTER);
                textView.setMaxLines(2);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(20);
                return textView;
            }
        });
        if (mHandler == null) {
            mHandler = new Handler();
        }
        textSwitcher.setText(texts[mIndex]);
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mIndex++;
                mIndex = mIndex == texts.length ? 0 : mIndex;
                textSwitcher.setText(texts[mIndex]);

                mHandler.postDelayed(this, 5000);
            }
        };
        mHandler.postDelayed(mRunnable, 5000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }
}
