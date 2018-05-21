package com.akhh.aungkhanthtoo.androidforall;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.akhh.aungkhanthtoo.androidforall.data.MySQLiteHelper;
import com.akhh.aungkhanthtoo.androidforall.data.Quiz;

import java.util.Random;

public class QuizActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "QuizActivity";

    private Animator mAnim;
    private ImageSwitcher mSwitcher;
    private TextView mTitleTextView;
    private TextView mQuestionTextView;
    private Button mTrueButton;
    private Button mFalseButton;
    private static final int[] truePics = {R.drawable.true1, R.drawable.true2};
    private static final int[] falsePics = {R.drawable.false1, R.drawable.false2};
    private static final String[] trueTitles = {"Yay!", "Great!"};
    public static final String[] falseTitles = {"Oops", "Ohh!"};
    private SQLiteOpenHelper mDBHelper;
    private Quiz mQuiz;
    private boolean mAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //checkToTest();
        checkForQuiz();

        setContentView(R.layout.activity_quiz);
        mTitleTextView = findViewById(R.id.textView_quiz_title);

        mQuestionTextView = findViewById(R.id.textView_question);

        if (mQuiz != null) {
            mTitleTextView.setText(mQuiz.getTitle());
            mQuestionTextView.setText(mQuiz.getQuestion());
            mAnswer = mQuiz.getAnswer() == 1;
        }

        mTrueButton = findViewById(R.id.button_true);
        mFalseButton = findViewById(R.id.button_false);
        mFalseButton.setOnClickListener(this);
        mTrueButton.setOnClickListener(this);

        setUpSwitcher();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpFab();
    }

    private void setUpFab() {
        final FloatingActionButton fab = findViewById(R.id.fab_go);
        fab.setOnClickListener(this);
        fab.post(new Runnable() {
            @Override
            public void run() {
                int cx = fab.getWidth() / 2;
                int cy = fab.getHeight() / 2;

                // Get the initial radius for clipping circle
                float radius = (float) Math.hypot(cx, cy);

                // Create the animation, the final radius is zero
                mAnim =
                        ViewAnimationUtils.createCircularReveal(fab, cx, cy, 0, radius);

                // When animation is done, make the view invisible.
                mAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        fab.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void setUpSwitcher() {
        mSwitcher =  findViewById(R.id.imageSwitcher);
        mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new
                        ImageSwitcher.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.WRAP_CONTENT));
                myView.setPadding(0, 50, 0, 0);
                return myView;
            }
        });
        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        mSwitcher.setInAnimation(in);
        mSwitcher.setImageResource(R.drawable.teacher);
    }

    private void checkToTest() {

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
                    final String title = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TITLE));
                    final String question = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_QUESTION));
                    Log.d(TAG, "checkToTest: "+question);
                    final int answer = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ANSWER));
                    final String explanation = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EXPLANATION));
                    final long id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID));
                    mQuiz = new Quiz(id, title, question, explanation, answer);
                }else{
                    Intent intent = new Intent(QuizActivity.this, FundamentalActivity.class);
                    startActivity(intent);
                    finish();
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

    private void checkForQuiz(){
        SharedPreferences sharedPreferences = getSharedPreferences(FundamentalActivity.QUIZ_PREFERENCE, MODE_PRIVATE);
        long id = sharedPreferences
                .getLong(MySQLiteHelper.COLUMN_ID, 0);
        if (id == 0) {
            Intent intent = new Intent(QuizActivity.this, FundamentalActivity.class);
            startActivity(intent);
            finish();
        }else{
            mQuiz = new Quiz(
                    id,
                   sharedPreferences.getString(MySQLiteHelper.COLUMN_TITLE, ""),
                    sharedPreferences.getString(MySQLiteHelper.COLUMN_QUESTION, ""),
                    sharedPreferences.getString(MySQLiteHelper.COLUMN_EXPLANATION, ""),
                    sharedPreferences.getInt(MySQLiteHelper.COLUMN_ANSWER, 0)
            );
            mDBHelper = new MySQLiteHelper(this);
        }
    }


    private void updateUi(String[] titles, int[] pictures, boolean b){
        int random = getRandom();
        mSwitcher.setImageResource(pictures[random]);
        mTitleTextView.setText(titles[random]);
        String prefix;
        int color;
        if(b){
            prefix = "That's right.\n";
            color = Color.parseColor("#34a853");
        }else{
            prefix = "Actually,\n";
            color = Color.parseColor("#ea4335");
        }
        mQuestionTextView.setText(prefix + mQuiz.getExplanation());
        mTitleTextView.setTextColor(color);
        mAnim.start();
        mFalseButton.setEnabled(false);
        mTrueButton.setEnabled(false);
        updateDatabase(b);
    }

    private int getRandom(){
        Random random = new Random();
        return random.nextInt(2);
    }

    private void updateDatabase(boolean b){
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        int testScore = b ? 1 : -1;
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TEST, testScore);

        try {
            int update = database.update(MySQLiteHelper.TABLE_NAME,
                    values, MySQLiteHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(mQuiz.getId())});
            Log.d(TAG, "updateDatabase: with numRows " + update);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.close();
            getSharedPreferences(FundamentalActivity.QUIZ_PREFERENCE, MODE_PRIVATE)
                    .edit()
                    .putLong(MySQLiteHelper.COLUMN_ID, 0)
                    .apply();
        }
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        switch (id){
            case R.id.button_true:
                if (mAnswer) {
                    updateUi(trueTitles, truePics, true);
                }else{
                    updateUi(falseTitles, falsePics, false);
                }

                break;
            case R.id.button_false:
                if(!mAnswer){
                    updateUi(trueTitles, truePics, true);
                }else{
                    updateUi(falseTitles, falsePics, false);
                }
                break;
            case R.id.fab_go:
                Intent intent = new Intent(QuizActivity.this, FundamentalActivity.class);
                startActivity(intent);
                finish();
            default:
                // Empty
        }

    }
}
