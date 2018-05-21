package com.akhh.aungkhanthtoo.androidforall.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.akhh.aungkhanthtoo.androidforall.R;

import java.util.List;

/**
 * Created by Lenovo on 2/7/2018.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "MySQLiteHelper";
    private static final String DATABASE_NAME = "AndroidForAll";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "vocabs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_VISIT = "visit";
    public static final String COLUMN_GET = "get";
    public static final String COLUMN_TEST = "test";
    public static final String COLUMN_QUESTION = "question";
    public static final String COLUMN_ANSWER = "answer";
    public static final String COLUMN_EXPLANATION = "explanation";


    private List<Vocab> mList;
    private String[] questions;
    private String[] explanations;
    private int[] answers;

    public MySQLiteHelper(Context context, List<Vocab> vocabs) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mList = vocabs;
        questions = context.getResources().getStringArray(R.array.questions);
        explanations = context.getResources().getStringArray(R.array.explanations);
        answers = context.getResources().getIntArray(R.array.answers);
    }

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createTable =
                "CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_TITLE + " TEXT, " + COLUMN_VISIT + " INTEGER DEFAULT 0, " +
                        COLUMN_GET + " INTEGER DEFAULT 0, " + COLUMN_TEST + " INTEGER DEFAULT 0, " +
                        COLUMN_QUESTION + " TEXT, " + COLUMN_ANSWER + " INTEGER, " +
                        COLUMN_EXPLANATION + " TEXT );";
        try {
            db.execSQL(createTable);
            Log.d(TAG, "onCreate: Successfully created.");
            ContentValues values = new ContentValues();
            for (int i = 0; i < mList.size(); i++) {
                values.put(COLUMN_TITLE, mList.get(i).getTitle());
                if (i < questions.length) {
                    values.put(COLUMN_QUESTION, questions[i]);
                    values.put(COLUMN_EXPLANATION, explanations[i]);
                    values.put(COLUMN_ANSWER, answers[i]);
                }else{
                    values.put(COLUMN_QUESTION, "Coming soon.");
                }
                long id = db.insert(TABLE_NAME, null, values);
                Log.d(TAG, "onCreate: Row id : " + id);
            }

    } catch(
    SQLException e)

    {
        Log.e(TAG, "onCreate: ", e);
    }

}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
