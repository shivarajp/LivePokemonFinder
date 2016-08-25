package com.shivaraj.friendz.shivaraj.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivaraj.friendz.R;
import com.shivaraj.friendz.shivaraj.adapters.ChatAdapter;
import com.shivaraj.friendz.shivaraj.geofire.GeoFire;
import com.shivaraj.friendz.shivaraj.geofire.GeoLocation;
import com.shivaraj.friendz.shivaraj.geofire.GeoQuery;
import com.shivaraj.friendz.shivaraj.geofire.GeoQueryEventListener;
import com.shivaraj.friendz.shivaraj.models.ChatMessageModel;
import com.shivaraj.friendz.shivaraj.models.User;
import com.shivaraj.friendz.shivaraj.utils.Constants;
import com.shivaraj.friendz.shivaraj.utils.PokemonSharedPreferencesManager;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.shivaraj.friendz.shivaraj.utils.Constants.ADDRESS;
import static com.shivaraj.friendz.shivaraj.utils.Constants.LATTITUDE;
import static com.shivaraj.friendz.shivaraj.utils.Constants.LONGITUDE;
import static com.shivaraj.friendz.shivaraj.utils.utils.DAILY_POSTS;
import static com.shivaraj.friendz.shivaraj.utils.utils.getTodaysKey;

public class ChatActivity extends BaseActivity implements GeoQueryEventListener {

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseChatRef;
    // [END define_database_reference]

    private GeoFire geoFire;
    private GeoQuery geoQuery;
    public GeoLocation mCurrentGeoLocation = new GeoLocation(37.7749, 122.4194);
    private ChatAdapter mMessagesAdapter;
    private RecyclerView mRecycler;

    ArrayList<ChatMessageModel> mMessages = new ArrayList<>();
    EditText messageEt;
    ImageView sendMessageIb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Chat Around");

        messageEt = (EditText) findViewById(R.id.etNewMsg);
        sendMessageIb = (ImageView) findViewById(R.id.ivSend);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseChatRef = FirebaseDatabase.getInstance().getReference("/chat");
        // setup GeoFire
        geoFire = new GeoFire(mDatabase.child(DAILY_POSTS + getTodaysKey(this) + "/chat_geofire"));

        mCurrentGeoLocation = new GeoLocation(getMyLocFromSharePref(this).latitude,
                getMyLocFromSharePref(this).longitude);

        // radius in km
        geoQuery = this.geoFire.queryAtLocation(mCurrentGeoLocation, PokemonSharedPreferencesManager.getFloat(this, Constants.DISTANCE));

        mRecycler = (RecyclerView) findViewById(R.id.rvChatConv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setHasFixedSize(true);

        mMessagesAdapter = new ChatAdapter(this, mMessages);
        mRecycler.setAdapter(mMessagesAdapter);

        sendMessageIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    //Store message to firebase
    private void sendMessage() {
        final String msg = messageEt.getText().toString();
        if (!msg.equals("")) {
            final String key = mDatabaseChatRef.push().getKey();
            messageEt.setText("");

            //geoFire.setLocation(key, geoLocation);
            final String userId = getUid();

            //Get the userID
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            User user = dataSnapshot.getValue(User.class);

                            // [START_EXCLUDE]
                            if (user == null) {

                                Toast.makeText(ChatActivity.this,
                                        "Error: could not fetch user.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // Write new post
                                ChatMessageModel msgModel = new ChatMessageModel(getUid(), user.username, msg, key);
                                Map<String, Object> postValues = msgModel.toMap();
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/" + key, postValues);
                                GeoLocation geoLocation = new GeoLocation(PokemonSharedPreferencesManager.getFloat(ChatActivity.this, Constants.LATTITUDE),
                                        PokemonSharedPreferencesManager.getFloat(ChatActivity.this, Constants.LONGITUDE));
                                mDatabaseChatRef.updateChildren(childUpdates);
                                geoFire.setLocation(key, geoLocation);
                            }
                            // [END_EXCLUDE]
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("", "getUser:onCancelled", databaseError.toException());
                        }
                    });
            // [END single_value_read]
        } else {
            Toast.makeText(getApplicationContext(), R.string.text_empty_message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        geoQuery.addGeoQueryEventListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        geoQuery.removeAllListeners();
    }


    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        Log.d("geoevent", key);
        DatabaseReference mDatabaseChatTemp = FirebaseDatabase.getInstance().getReference("/chat/" + key);
        mDatabaseChatTemp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatMessageModel chatMsg = new ChatMessageModel();
                chatMsg = dataSnapshot.getValue(ChatMessageModel.class);
                mMessages.add(chatMsg);
                //mMessagesAdapter.notifyItemRangeChanged(mMessages.size()-4, 3);
                mMessagesAdapter.notifyDataSetChanged();
                mRecycler.smoothScrollToPosition(mMessages.size() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "error" + databaseError, Toast.LENGTH_LONG).show();
            }
        });

    }

    //Called from
    @Subscribe
    public void loadMessagesInList(final ChatMessageModel msg) {
        mMessages.add(msg);
        mMessagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onKeyExited(String key) {

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }

    private void setMyLocationToSharePref(float lat, float lon) {
        PokemonSharedPreferencesManager.putFloat(this, LATTITUDE, lat);
        PokemonSharedPreferencesManager.putFloat(this, LONGITUDE, lon);
    }

    private String getMyAddress(Context context) {
        return PokemonSharedPreferencesManager.getString(this, ADDRESS);
    }

    private void setMyAddress(Context context, String address) {
        if (!address.equals(""))
            PokemonSharedPreferencesManager.putString(this, ADDRESS, address);
    }

    private LatLng getMyLocFromSharePref(Context context) {
        return new LatLng(PokemonSharedPreferencesManager.getFloat(context, LATTITUDE),
                PokemonSharedPreferencesManager.getFloat(context, LONGITUDE));
    }
}
