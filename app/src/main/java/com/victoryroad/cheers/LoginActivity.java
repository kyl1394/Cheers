package com.victoryroad.cheers;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener {
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private String userId;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        hideStatusBar();

        if (isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_login);
            callbackManager = CallbackManager.Factory.create();

            loginButton = (LoginButton) findViewById(R.id.login_button);

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            if (Profile.getCurrentProfile() == null) {
                                profileTracker = new ProfileTracker() {
                                    @Override
                                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                        login(profile2);
                                    }
                                };
                            } else {
                                login(Profile.getCurrentProfile());
                            }
                        }

                        public void login(Profile profile2) {
                            userId = profile2.getId();
                            checkAndAddUser();

                            loginButton.setVisibility(View.INVISIBLE); //<- IMPORTANT
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();//<- IMPORTANT
                        }
                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                        }
                    });

            TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/appo_paint.otf");
            titleTextView.setTypeface(typeface);
        }
    }

    /******************************************************************************
     *
     * Checks Database for Facebook user ID number
     * If the user is not found a new entry is created.
     *
     *****************************************************************************/
    private void checkAndAddUser() {
        final DatabaseReference firebaseDbRef = FirebaseDatabase.getInstance().getReference("Users");

        firebaseDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(userId)) { // check if the user is in our DB
                    firebaseDbRef.child(userId).setValue("{\"Name\":\"" + Profile.getCurrentProfile().getName() + "\",\"Checkins\":{}");  // add user since they don't exist
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
