package com.projects.aungkhanthtoo.bookshop;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.projects.aungkhanthtoo.bookshop.data.Author;
import com.projects.aungkhanthtoo.bookshop.data.AuthorsResponse;
import com.projects.aungkhanthtoo.bookshop.data.BooksResponse;
import com.projects.aungkhanthtoo.bookshop.data.Genre;
import com.projects.aungkhanthtoo.bookshop.data.GenresResponse;
import com.projects.aungkhanthtoo.bookshop.data.MyBookAdapter;
import com.projects.aungkhanthtoo.bookshop.data.Publisher;
import com.projects.aungkhanthtoo.bookshop.data.PublishersResponse;
import com.projects.aungkhanthtoo.bookshop.network.RetrofitHelper;
import com.projects.aungkhanthtoo.bookshop.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookEditorActivity extends AppCompatActivity {

    private static final String TAG = "BookEditorActivity";
    private static final int REQUEST_IMAGE = 813;
    private static final int REQUEST_PDF = 42;
    private static final int REQUEST_STORAGE = 10;
    private ImageView mImage;
    private TextInputEditText mPdfEditText, mCodeEditText, mNameEditText, mDescriptionEditText, mEditionEditText, mPriceEditText;
    private RetrofitHelper mRetrofit;
    private List<Genre> genres;
    private List<Author> authors;
    private List<Publisher> publishers;
    private Spinner mGenreSpinner, mAuthorSpinner, mPublisherSpinner;
    private Bitmap bitmap;
    private Uri mImageUri, mPdfUri;
    private Button mCreateButtton;
    private boolean mImageRequested;
    private View mProgressView;
    private boolean mIsEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRetrofit = new RetrofitHelper();
        setContentView(R.layout.activity_book_editor);
        findMyViews();
        if (hasBookId())
            updateUI();
        else
            initSpinners();
    }

    private void updateUI() {
        mCreateButtton.setText(R.string.update_button_label);
        mIsEditing = true;
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Edit Book");
        bindExtras();
        showSelectedSpinners();
    }

    private boolean hasBookId() {
        return !getData(MyBookAdapter.EXTRA_ID).equals("0");
    }

    private void findMyViews() {
        mProgressView = findViewById(R.id.upload_progress);
        mCreateButtton = findViewById(R.id.btn_create_update_book);
        mCreateButtton.setOnClickListener(view -> attemptToUpload());
        mCodeEditText = findViewById(R.id.edtCodeNo);
        mNameEditText = findViewById(R.id.edtName);
        mDescriptionEditText = findViewById(R.id.edtDescription);
        mEditionEditText = findViewById(R.id.edtEdition);
        mPriceEditText = findViewById(R.id.edtPrice);
        mGenreSpinner = findViewById(R.id.spinner_genre);
        mAuthorSpinner = findViewById(R.id.spinner_author);
        mPublisherSpinner = findViewById(R.id.spinner_publisher);
        mPdfEditText = findViewById(R.id.edtPdf);
        mPdfEditText.setOnClickListener(view -> choosePdfFile());
        mImage = findViewById(R.id.image_create_edit_book);
        mImage.setOnClickListener(view -> chooseImage());
    }

    private void initSpinners() {
        populateAuthorSpinner();
        populateGenreSpinner();
        populatePublisherSpinner();
    }

    private void bindExtras() {
        showImage(mImage);
        mCodeEditText.setText(getData(MyBookAdapter.EXTRA_NO));
        mNameEditText.setText(getData(MyBookAdapter.EXTRA_TITLE));
        mDescriptionEditText.setText(getData(MyBookAdapter.EXTRA_DESCRIPT));
        mPdfEditText.setText(getData(MyBookAdapter.EXTRA_PDF));
        mEditionEditText.setText(getData(MyBookAdapter.EXTRA_EDITION));
        mPriceEditText.setText(getData(MyBookAdapter.EXTRA_PRICE));
    }

    private void showSelectedSpinners() {
        populateGenreSpinner(getId(MyBookAdapter.EXTRA_GENRE_ID));
        populateAuthorSpinner(getId(MyBookAdapter.EXTRA_AUTHOR_ID));
        populatePublisherSpinner(getId(MyBookAdapter.EXTRA_PUBLISHER_ID));
    }

    private View focusView;

    private void showImage(ImageView imageView) {
        Glide.with(this)
                .load(getData(MyBookAdapter.EXTRA_IMAGE))
                .into(imageView);
    }

    private String getData(String key) {
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            return bundle.getString(key);
        }
        return "0";
    }

    private int getId(String key) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            return bundle.getInt(key, 0);
        }
        throw new IllegalStateException("Bundle is null");
    }

    private void attemptToUpload() {
        if (focusView != null) {
            ((TextInputEditText) focusView).setError(null);
        }
        if (!isValid(mCodeEditText)) {
            focusView = mCodeEditText;
        } else if (!isValid(mNameEditText)) {
            focusView = mNameEditText;
        } else if (!isValid(mDescriptionEditText)) {
            focusView = mDescriptionEditText;
        } else if (!isValid(mPdfEditText)) {
            focusView = mPdfEditText;
        } else if (!isValid(mEditionEditText)) {
            focusView = mEditionEditText;
        } else if (!isValid(mPriceEditText)) {
            focusView = mPriceEditText;
        } else if (bitmap == null && !mIsEditing) {
            Toast.makeText(this, "Image is required to upload!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            uploadBook(
                    mCodeEditText.getText().toString(),
                    mNameEditText.getText().toString(),
                    mDescriptionEditText.getText().toString(),
                    mPriceEditText.getText().toString(),
                    mEditionEditText.getText().toString()
            );
            return;
        }
        focusView.requestFocus();
        ((TextInputEditText) focusView).setError("This field is required");
    }

    private void uploadBook(String code, String name, String description, String price, String edition) {
        final RequestBody codePart = createPartFromString(code);
        final RequestBody namePart = createPartFromString(name);
        final RequestBody descriptionPart = createPartFromString(description);
        final RequestBody pricePart = createPartFromString(price);
        final RequestBody editionPart = createPartFromString(edition);
        final String authorId = String.valueOf(authors.get(mAuthorSpinner.getSelectedItemPosition()).getId());
        final String genreId = String.valueOf(genres.get(mGenreSpinner.getSelectedItemPosition()).getId());
        final String publisherId = String.valueOf(authors.get(mPublisherSpinner.getSelectedItemPosition()).getId());
        final RequestBody authorPart = createPartFromString(authorId);
        final RequestBody genrePart = createPartFromString(genreId);
        final RequestBody publisherPart = createPartFromString(publisherId);

        final MultipartBody.Part imagePart, pdfPart;
        if (mPdfUri == null) {
            pdfPart = null; // It Doesn't work to upload empty for updating.
        } else {            // You always need image and pdf to re-upload even though you only want to update the book name.
                                                                // What a goddamn API!
            pdfPart = prepareFilePart("pdf", mPdfUri);
        }
        if (mImageUri == null) {
            imagePart = null;
        } else {
            imagePart = prepareFilePart("image", mImageUri);
        }

        if (mIsEditing) {
            RequestBody idPart = createPartFromString(getData(MyBookAdapter.EXTRA_ID));
            Call<BooksResponse> call = mRetrofit.updateBook(
                    idPart, codePart, namePart, descriptionPart, pricePart, editionPart,
                    imagePart, pdfPart, authorPart, genrePart, publisherPart
            );
            showProgress(true);
            call.enqueue(new Callback<BooksResponse>() {
                @Override
                public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                    if (response.isSuccessful()) {
                        showProgress(false);
                        if (response.body().getStatus() == 1) {
                            Toast.makeText(BookEditorActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(BookEditorActivity.this, "Updating fails!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: " + response.body().getStatus() + " / " + response.body().getMessage());
                        }
                    } else {
                        Log.e(TAG, "Server fails!");
                    }
                }

                @Override
                public void onFailure(Call<BooksResponse> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                }
            });

            return;
        }
        Call<BooksResponse> call = mRetrofit.createBook(
                codePart, namePart, descriptionPart, pricePart, editionPart, imagePart, pdfPart, authorPart, genrePart, publisherPart
        );
        showProgress(true);
        call.enqueue(new Callback<BooksResponse>() {
            @Override
            public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                if (response.isSuccessful()) {
                    BooksResponse body = response.body();
                    showProgress(false);
                    if (body.getStatus() == 1) {
                        Toast.makeText(BookEditorActivity.this, "New book is successfully created.", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {

                    }
                } else {
                    Log.e(TAG, "Server fails!");
                }
            }

            @Override
            public void onFailure(Call<BooksResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
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


    private boolean isValid(TextInputEditText editText) {
        return !TextUtils.isEmpty(editText.getText().toString());
    }

    private void choosePdfFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent open = new Intent(Intent.ACTION_GET_CONTENT);
                open.setType("application/pdf");
                open.addCategory(Intent.CATEGORY_OPENABLE);
                if (open.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(open, REQUEST_PDF);
                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "External Storage is needed to upload pdf file.", Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE);
                mImageRequested = false;
            }
        }
    }

    private void chooseImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent open = new Intent(Intent.ACTION_GET_CONTENT);
                open.setType("image/*");
                if (open.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(open, REQUEST_IMAGE);
                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "External Storage is needed to upload image file.", Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE);
                mImageRequested = true;
            }
        }
    }


    private void populateGenreSpinner() {
        genres = new ArrayList<>();
        Call<GenresResponse> call = mRetrofit.getGenres();
        call.enqueue(new Callback<GenresResponse>() {
            @Override
            public void onResponse(Call<GenresResponse> call, Response<GenresResponse> response) {
                if (response.isSuccessful()) {
                    GenresResponse body = response.body();
                    genres.addAll(body.getResult());
                    final List<String> names = new ArrayList<>();
                    for (Genre result :
                            genres) {
                        names.add(result.getGenreName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(BookEditorActivity.this, android.R.layout.simple_spinner_dropdown_item, names);
                    mGenreSpinner.setAdapter(adapter);
                } else {
                    Log.e(TAG, "Server fails!");
                }
            }

            @Override
            public void onFailure(Call<GenresResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void populateGenreSpinner(int id) {
        Call<GenresResponse> call = mRetrofit.getGenres();
        call.enqueue(new Callback<GenresResponse>() {
            @Override
            public void onResponse(Call<GenresResponse> call, Response<GenresResponse> response) {
                if (response.isSuccessful()) {
                    GenresResponse body = response.body();
                    genres.addAll(body.getResult());
                    final List<String> names = new ArrayList<>();
                    int position = -1;
                    for (int i = 0; i < body.getResult().size(); i++) {
                        names.add(body.getResult().get(i).getGenreName());
                        if (id == body.getResult().get(i).getId()) {
                            position = i;
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(BookEditorActivity.this, android.R.layout.simple_spinner_dropdown_item, names);
                    mGenreSpinner.setAdapter(adapter);
                    mGenreSpinner.setSelection(position);
                } else {
                    Log.e(TAG, "Server fails!");
                }
            }

            @Override
            public void onFailure(Call<GenresResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void populateAuthorSpinner() {
        authors = new ArrayList<>();
        Call<AuthorsResponse> call = mRetrofit.getAuthors();
        call.enqueue(new Callback<AuthorsResponse>() {
            @Override
            public void onResponse(Call<AuthorsResponse> call, Response<AuthorsResponse> response) {
                if (response.isSuccessful()) {
                    AuthorsResponse body = response.body();
                    authors.addAll(body.getResult());
                    final List<String> names = new ArrayList<>();
                    for (Author result :
                            authors) {
                        names.add(result.getAuthorName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(BookEditorActivity.this, android.R.layout.simple_spinner_dropdown_item, names);
                    mAuthorSpinner.setAdapter(adapter);
                } else {
                    Log.e(TAG, "Server fails!");
                }
            }

            @Override
            public void onFailure(Call<AuthorsResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void populateAuthorSpinner(int id) {
        Call<AuthorsResponse> call = mRetrofit.getAuthors();
        call.enqueue(new Callback<AuthorsResponse>() {
            @Override
            public void onResponse(Call<AuthorsResponse> call, Response<AuthorsResponse> response) {
                if (response.isSuccessful()) {
                    AuthorsResponse body = response.body();
                    authors.addAll(body.getResult());
                    final List<String> names = new ArrayList<>();
                    int position = -1;
                    for (int i = 0; i < body.getResult().size(); i++) {
                        names.add(body.getResult().get(i).getAuthorName());
                        if (id == body.getResult().get(i).getId()) {
                            position = i;
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(BookEditorActivity.this, android.R.layout.simple_spinner_dropdown_item, names);
                    mAuthorSpinner.setAdapter(adapter);
                    mAuthorSpinner.setSelection(position);
                } else {
                    Log.e(TAG, "Server fails!");
                }
            }

            @Override
            public void onFailure(Call<AuthorsResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void populatePublisherSpinner() {
        publishers = new ArrayList<>();
        Call<PublishersResponse> call = mRetrofit.getPublishers();
        call.enqueue(new Callback<PublishersResponse>() {
            @Override
            public void onResponse(Call<PublishersResponse> call, Response<PublishersResponse> response) {
                if (response.isSuccessful()) {
                    PublishersResponse body = response.body();
                    publishers.addAll(body.getResult());
                    final List<String> names = new ArrayList<>();
                    for (Publisher result :
                            publishers) {
                        names.add(result.getPname());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(BookEditorActivity.this, android.R.layout.simple_spinner_dropdown_item, names);
                    mPublisherSpinner.setAdapter(adapter);
                } else {
                    Log.e(TAG, "Server fails!");
                }
            }

            @Override
            public void onFailure(Call<PublishersResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void populatePublisherSpinner(int id) {
        Call<PublishersResponse> call = mRetrofit.getPublishers();
        call.enqueue(new Callback<PublishersResponse>() {
            @Override
            public void onResponse(Call<PublishersResponse> call, Response<PublishersResponse> response) {
                if (response.isSuccessful()) {
                    PublishersResponse body = response.body();
                    publishers.addAll(body.getResult());
                    final List<String> names = new ArrayList<>();
                    int position = -1;
                    for (int i = 0; i < body.getResult().size(); i++) {
                        names.add(body.getResult().get(i).getPname());
                        if (id == body.getResult().get(i).getId()) {
                            position = i;
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(BookEditorActivity.this, android.R.layout.simple_spinner_dropdown_item, names);
                    mPublisherSpinner.setAdapter(adapter);
                    mPublisherSpinner.setSelection(position);
                } else {
                    Log.e(TAG, "Server fails!");
                }
            }

            @Override
            public void onFailure(Call<PublishersResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mImageRequested) {
                    chooseImage();
                } else {
                    choosePdfFile();
                }
            } else {
                Toast.makeText(this, "Permission was not granted.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE) {
                mImageUri = data.getData();
                showBookCover(mImageUri);
            }
            if (requestCode == REQUEST_PDF) {
                mPdfUri = data.getData();
                showPdfName(mPdfUri);
            }
        }
    }

    private void showPdfName(Uri pdfUri) {
        prepareFilePart("pdf", pdfUri);
    }


    private void showBookCover(Uri fullPhotoUri) {
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(fullPhotoUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap = BitmapFactory.decodeStream(inputStream);
        if (bitmap.getHeight() > 3000) {
            Toast toast = Toast.makeText(this, "Image can't be uploaded because of its big size.\n Please select another image.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            return;
        }
        mImage.setImageBitmap(bitmap);
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);
        Log.d(TAG, file + "");

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        file
                );

        if (file.getName().contains(".pdf")) {
            mPdfEditText.setText(file.getName());
        }

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

}
