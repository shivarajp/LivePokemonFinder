package com.shivaraj.friendz.shivaraj.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivaraj.friendz.R;
import com.shivaraj.friendz.shivaraj.PokemongoApplication;
import com.shivaraj.friendz.shivaraj.geofire.GeoFire;
import com.shivaraj.friendz.shivaraj.geofire.GeoLocation;
import com.shivaraj.friendz.shivaraj.models.Post;
import com.shivaraj.friendz.shivaraj.models.User;
import com.shivaraj.friendz.shivaraj.utils.utils;

import java.util.HashMap;
import java.util.Map;

import static com.shivaraj.friendz.shivaraj.utils.utils.DAILY_POSTS;
import static com.shivaraj.friendz.shivaraj.utils.utils.getRandomImg;

public class NewPostActivity extends BaseActivity {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";
    private static final String GEO_FIRE_REF = "/geofire";

    private GeoFire geoFire;

    //private InterstitialAd mInterstitialAd;

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    private DatabaseReference mGeoFireLocRef;
    // [END declare_database_ref]

    private EditText mTitleField;
    private EditText mBodyField;
    private String randomImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.nav_newpost);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mGeoFireLocRef = FirebaseDatabase.getInstance().getReference(DAILY_POSTS + utils.getTodaysKey(this) + GEO_FIRE_REF);
        // [END initialize_database_ref]

        /*mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Constants.INTERSTITIAL_NEWPOST_ID);*/
        mTitleField = (EditText) findViewById(R.id.field_title);

        mBodyField = (EditText) findViewById(R.id.field_body);

        findViewById(R.id.fab_submit_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
        /*requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                // beginSecondActivity();
            }
        });*/
    }

    /*private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }
*/
    private void submitPost() {
        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, user.username, title, body);
                            /*if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            }*/
                        }

                        // Finish this Activity, back to the stream
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
        // [END single_value_read]
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String username, String title, String body) {

        // setup GeoFire
        this.geoFire = new GeoFire(mGeoFireLocRef);

        String key = mDatabase.child("posts").push().getKey();
        GeoLocation geoLocation = PokemongoApplication.getCurrentLocation();

        this.geoFire.setLocation(key, geoLocation);
        //Stores loc at geofire as well to show on map
        storeAtGeofire(key, geoLocation);

        String imgUrl = getRandomImg();

        Post post = new Post(userId, username, title, body, geoLocation.latitude + "," + geoLocation.longitude, key, System.currentTimeMillis(),imgUrl);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        //childUpdates.put(DAILY_POSTS + utils.getTodaysKey(this) + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);

    }

    private void storeAtGeofire(String key, GeoLocation geoLocation) {
        DatabaseReference mGeoFireLocRef2 = FirebaseDatabase.getInstance().getReference(GEO_FIRE_REF);
        GeoFire mGeoFire = new GeoFire(mGeoFireLocRef2);
        mGeoFire.setLocation(key, geoLocation);
    }
    // [END write_fan_out]


    @Override
    public void onBackPressed() {
        super.onBackPressed();
       /* if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        finish();*/
    }


}
