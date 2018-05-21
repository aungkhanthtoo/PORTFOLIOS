package com.akhh.aungkhanthtoo.androidforall;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.akhh.aungkhanthtoo.androidforall.data.MySQLiteHelper;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Lenovo on 2/5/2018.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingsFragment";

    private TextView userProgressText;
    private TextView userScoreText;

    /**
     * Called during {@link #onCreate(Bundle)} to supply the preferences for this fragment.
     * Subclasses are expected to call {@link #setPreferenceScreen(PreferenceScreen)} either
     * directly or via helper methods such as {@link #addPreferencesFromResource(int)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the
     *                           {@link PreferenceScreen} with this key.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Button button = null;
        if (view != null) {
            button = view.findViewById(R.id.button_sign_out);
        }
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirm")
                            .setMessage("Are you sure to sign out and exit?")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AuthUI.getInstance()
                                            .signOut(getContext())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Intent intent = new Intent(getActivity(), AuthUiSignInActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    getActivity().finishAffinity();
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();

                }
            });
        }
        TextView userTextName = view.findViewById(R.id.textView_user_name);
        CircleImageView imageView = view.findViewById(R.id.user_image);
        loadProfile(userTextName, imageView);
        userProgressText = view.findViewById(R.id.textView_user_progress);
        userScoreText = view.findViewById(R.id.textView_user_score);
        MyProgressAsyncTask asyncTask = new MyProgressAsyncTask(getContext(), userProgressText, userScoreText);
        asyncTask.execute();
        return view;
    }


    static class MyProgressAsyncTask extends AsyncTask<Void, Void, List<Integer>> {
        private Context mContext;
        private TextView mProgressView;
        private TextView mScoreView;

        public MyProgressAsyncTask(Context context, TextView progress, TextView score) {
            mContext = context;
            mProgressView = progress;
            mScoreView = score;
        }

        @Override
        protected List<Integer> doInBackground(Void... voids) {
            int progress = getFromDatabase(" != ?", new String[]{"0"});

            int score = getFromDatabase(" == ?", new String[]{"1"});
            Log.d(TAG, "doInBackground: score " + score);

            ArrayList<Integer> list = new ArrayList<>(2);
            list.add(progress);
            list.add(score);
            return list;
        }

        private int getFromDatabase(String s, String[] strings) {
            int count = 0;
            try (SQLiteDatabase readableDatabase = new MySQLiteHelper(mContext).getReadableDatabase();
                 Cursor query = readableDatabase.query(
                         MySQLiteHelper.TABLE_NAME,
                         new String[]{MySQLiteHelper.COLUMN_ID},
                         MySQLiteHelper.COLUMN_TEST + s,
                         strings,
                         null, null, null
                 )) {
                if (query != null) {
                    count = query.getCount() > 0 ? query.getCount() : 0;

                } else {
                    Log.e(TAG, "doInBackground: Cursor is null.");
                }
            } catch (SQLException e) {
                Log.e(TAG, "doInBackground: ", e);
            }
            return count;
        }

        @Override
        protected void onPostExecute(List<Integer> integers) {
            Log.d(TAG, "onPostExecute: score " + integers.get(1));
            String progress = integers.isEmpty() ? "0" : integers.get(0).toString();
            String score = integers.isEmpty() ? "0" : integers.get(1).toString();
            Log.d(TAG, "onPostExecute: score Text " + score);
            mProgressView.setText(progress);
            mProgressView.append("%");
            final int progressValue = Integer.parseInt(progress);
            final int scoreValue = Integer.parseInt(score);
            float percent = 0;
            if (progressValue != 0) {
                percent = ((float) ((scoreValue * 100) / progressValue));
            }

            int scoreColor = getScoreColor(((int) Math.floor(((double) percent))));
            final GradientDrawable drawable = (GradientDrawable) mScoreView.getBackground();
            drawable.setColor(ContextCompat.getColor(mContext, scoreColor));
            mScoreView.setText(percent + "");
            mScoreView.append("%");
        }

        private int getScoreColor(int percent) {
            int color;
            if (percent < 40) {
                color = R.color.google_red;
            } else if (percent < 70) {
                color = R.color.google_yellow;
            } else {
                color = R.color.google_green;
            }
            return color;
        }
    }


    private void loadProfile(TextView userTextName, CircleImageView imageView) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getPhotoUrl() != null) {
            Picasso.with(getContext())
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.profile)
                    .noFade()
                    .into(imageView);
        }
        userTextName.setText(
                TextUtils.isEmpty(user.getDisplayName()) ? "No display name" : user.getDisplayName());
    }


}
