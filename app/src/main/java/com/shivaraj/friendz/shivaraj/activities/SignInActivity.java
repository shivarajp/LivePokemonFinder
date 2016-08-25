package com.shivaraj.friendz.shivaraj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shivaraj.friendz.R;
import com.shivaraj.friendz.shivaraj.models.User;
import com.shivaraj.friendz.shivaraj.utils.Constants;
import com.shivaraj.friendz.shivaraj.utils.PokemonSharedPreferencesManager;

import static com.shivaraj.friendz.shivaraj.utils.Constants.LATTITUDE;
import static com.shivaraj.friendz.shivaraj.utils.Constants.LONGITUDE;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private TextView mTitleTv;
    private TextView mChangeBtnTv;
    private EditText mEmailField;
    private EditText mPasswordField;
    private FloatingActionButton mSignInButton;
    private FloatingActionButton mSignUpButton;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (PokemonSharedPreferencesManager.getBoolean(this, Constants.IS_FIRST_TIME)) {
            if (mAuth.getCurrentUser() != null) {
                writeNewUser(mAuth.getCurrentUser().getUid(), usernameFromEmail(mAuth.getCurrentUser().getEmail()), mAuth.getCurrentUser().getEmail());
            }

            Intent mainActIntent = new Intent(SignInActivity.this, MainActivity.class);
            mainActIntent.putExtra("from", "signin");
            startActivity(mainActIntent);
            finish();
        }


        // Views
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        mTitleTv = (TextView) findViewById(R.id.titleTv);
        mChangeBtnTv = (TextView) findViewById(R.id.link_change_btn);
        mSignInButton = (FloatingActionButton) findViewById(R.id.button_sign_in);
        mSignUpButton = (FloatingActionButton) findViewById(R.id.button_sign_up);

        // Click listeners
        mSignInButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
        mChangeBtnTv.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        hideKeyboard();
        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, R.string.signin_err,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, R.string.signin_err,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        PokemonSharedPreferencesManager.putBoolean(this, Constants.IS_FIRST_TIME, true);
        PokemonSharedPreferencesManager.putString(this, Constants.UID, user.getUid());
        PokemonSharedPreferencesManager.putString(this, Constants.USER_EMAIL, user.getEmail());
        PokemonSharedPreferencesManager.putFloat(this, Constants.DISTANCE, 1);
        //Setting default location to SFO so that if no location is availiable default show from SFO
        setMyLocationToSharePref((float) 37.7749, (float) 122.4194);

        // Go to MainActivity
        Intent mainActIntent = new Intent(SignInActivity.this, MainActivity.class);
        mainActIntent.putExtra("from", "signin");
        startActivity(mainActIntent);
        finish();
    }

    private void setMyLocationToSharePref(float lat, float lon) {
        PokemonSharedPreferencesManager.putFloat(this, LATTITUDE, lat);
        PokemonSharedPreferencesManager.putFloat(this, LONGITUDE, lon);
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_in:
                signIn();
                break;
            case R.id.button_sign_up:
                signUp();
                break;
            case R.id.link_change_btn:
                updateButtons();
                break;
        }
    }

    private void updateButtons() {
        if (mSignUpButton.getVisibility() == View.VISIBLE) {
            mSignUpButton.setVisibility(View.GONE);
            mSignInButton.setVisibility(View.VISIBLE);
            mChangeBtnTv.setText(R.string.link_signup_msg);
            mTitleTv.setText(R.string.sign_in);
        } else if (mSignInButton.getVisibility() == View.VISIBLE) {
            mSignInButton.setVisibility(View.GONE);
            mSignUpButton.setVisibility(View.VISIBLE);
            mChangeBtnTv.setText(R.string.signin_link_msg);
            mTitleTv.setText(R.string.sign_up);
        }
    }

    public void hideKeyboard() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            getBaseContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
