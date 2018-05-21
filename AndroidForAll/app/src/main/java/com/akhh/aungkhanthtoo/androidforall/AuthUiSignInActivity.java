package com.akhh.aungkhanthtoo.androidforall;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuthUiSignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private static final String GOOGLE_PRIVACY_POLICY_URL = "https://www.google.com/policies/privacy/";
    private AnimationDrawable mAnimDraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_ui);
        Button button = findViewById(R.id.button_normal);
        mAnimDraw = ((AnimationDrawable) button.getBackground());


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mAnimDraw.start();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            startActivity(new Intent(this, QuizActivity.class));
            finish();
        } else {
            // not signed in
            // Create and launch sign-in intent
            if (isOnline()) {
                startAuthUiActivityForResult();
            }else{
                Toast.makeText(this, "NO INTERNET CONNECTION!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private boolean isOnline(){
        ConnectivityManager systemService = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = systemService.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

    }
    private void startAuthUiActivityForResult(){
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(R.style.GreenTheme)
                        .setLogo(R.drawable.firebase_auth_120dp)
                        .setAvailableProviders(getProviders())
                        .setTosUrl(GOOGLE_TOS_URL)
                        .setPrivacyPolicyUrl(GOOGLE_PRIVACY_POLICY_URL)
                        .setIsSmartLockEnabled(true,
                                true)
                        .build(),
                RC_SIGN_IN);
    }

    private List<AuthUI.IdpConfig> getProviders(){
        List<AuthUI.IdpConfig> list = new ArrayList<>();

        AuthUI.IdpConfig googleIdp = new AuthUI.IdpConfig.GoogleBuilder().
                setScopes(getGoogleScopes())
                .build();

        AuthUI.IdpConfig facebookIdp = new AuthUI.IdpConfig.FacebookBuilder()
                .setPermissions(Arrays.asList("user_friends"))
                .build();

        list.add(googleIdp);
        list.add(facebookIdp);
        return list;
    }

    private List<String> getGoogleScopes() {
        List<String> result = new ArrayList<>();
            result.add("https://www.googleapis.com/auth/youtube.readonly");
            result.add(Scopes.DRIVE_FILE);
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setContentView(R.layout.activity_auth_ui);

            if (requestCode == RC_SIGN_IN) {
                IdpResponse response = IdpResponse.fromResultIntent(data);

                // Successfully signed in
                if (resultCode == RESULT_OK) {

                    //startActivity to main screen;
                    startActivity(new Intent(this, QuizActivity.class));
                    finish();

                } else {
                    // Sign in failed
                    if (response == null) {

                        // User pressed back button

                        return;
                    }

                    if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {

                        // No network situation
                        return;
                    }

                    if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {

                        // Probably use touch the outside of dialog;

                    }

                }

            }

    }


    public void onButtonClick(View view) {
       startAuthUiActivityForResult();
    }
}
