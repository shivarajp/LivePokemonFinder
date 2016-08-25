package com.shivaraj.friendz.shivaraj.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shivaraj.friendz.R;
import com.shivaraj.friendz.shivaraj.PokemongoApplication;
import com.shivaraj.friendz.shivaraj.adapters.PostsAdapter;
import com.shivaraj.friendz.shivaraj.asynctasks.AsyncPostLoader;
import com.shivaraj.friendz.shivaraj.geofire.GeoFire;
import com.shivaraj.friendz.shivaraj.geofire.GeoLocation;
import com.shivaraj.friendz.shivaraj.geofire.GeoQuery;
import com.shivaraj.friendz.shivaraj.geofire.GeoQueryEventListener;
import com.shivaraj.friendz.shivaraj.models.Post;
import com.shivaraj.friendz.shivaraj.utils.Constants;
import com.shivaraj.friendz.shivaraj.utils.PokemonSharedPreferencesManager;
import com.shivaraj.friendz.shivaraj.utils.utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import static com.shivaraj.friendz.shivaraj.utils.utils.DAILY_POSTS;

public abstract class PostListFragment extends Fragment implements
        GeoQueryEventListener {

    private static final String TAG = "PostListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseTemp;
    // [END define_database_reference]


    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private static final GeoLocation INITIAL_CENTER = new GeoLocation(37.7749, 122.4194);
    public GeoLocation mCurrentGeoLocation = new GeoLocation(37.7749, 122.4194);
    private PostsAdapter mPostAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private ArrayList<Post> mPosts = new ArrayList<>();

    private static int dayCount = 0;
    private static int attempt = 0;
    private ArrayList<String> postKeys = new ArrayList<String>();
    private boolean alreadyRequested = false;
    private FirebaseAnalytics mFirebaseAnalytics;

    public PostListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // [END create_database_reference]
        // setup GeoFire
        this.geoFire = new GeoFire(mDatabase.child(DAILY_POSTS + utils.getTodaysKey(getActivity()) + "/geofire"));
        // radius in km
        this.geoQuery = this.geoFire.queryAtLocation(INITIAL_CENTER, PokemonSharedPreferencesManager.getFloat(getActivity(), Constants.DISTANCE));

        mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);

        mPostAdapter = new PostsAdapter(mPosts, getActivity());
        mRecycler.setAdapter(mPostAdapter);
        //this.geoQuery.addGeoQueryEventListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        Log.d("geoevent", key);
        postKeys.add(key);

        if (postKeys.size() < 20) {
            AsyncPostLoader asyncPlayer = new AsyncPostLoader();
            asyncPlayer.execute(key);
        }
    }

    /**
     * gets called after all events
     */
    @Override
    public void onGeoQueryReady() {
        Log.d("event", "ready");
/*
        if(postKeys.size() == 0 && attempt < 3 ){
            ++attempt;
            geoQuery.removeAllListeners();
            geoQuery.addGeoQueryEventListener(this);
            return;
        }else*/

        if (postKeys.size() == 0 && !alreadyRequested) {
            alreadyRequested = true;
            GeoFire geoFire2 = new GeoFire(mDatabase.child("/geofire"));
            GeoQuery geoQuery2 = geoFire2.queryAtLocation(INITIAL_CENTER, PokemonSharedPreferencesManager.getFloat(getActivity(), Constants.DISTANCE));
            geoQuery2.addGeoQueryEventListener(this);
        }

        /*if (postKeys.size() == 0 && dayCount > -3) {
            dayCount--;
            String previousKey = utils.getPreviousDateKey(getActivity(), dayCount);
            this.geoFire = new GeoFire(mDatabase.child(DAILY_POSTS + previousKey + "/geofire"));
            this.geoQuery = this.geoFire.queryAtLocation(INITIAL_CENTER, PokemonSharedPreferencesManager.getFloat(getActivity(), Constants.DISTANCE));
            geoQuery.addGeoQueryEventListener(this);
        } else if (postKeys.size() > 0 && postKeys.size() < 30) {
            //load 10 posts first
            for (int i = 0; i < postKeys.size(); i++) {
                AsyncPostLoader asyncPlayer = new AsyncPostLoader();
                asyncPlayer.execute(postKeys.get(i));
            }
        } else if (postKeys.size() > 30) {
            //More than 10 posts
            //Load 10 first then pass to rest to async task to load
            for (int i = 0; i < 10; i++) {
                AsyncPostLoader asyncPlayer = new AsyncPostLoader();
                asyncPlayer.execute(postKeys.get(i));
            }
        }*/
    }

    @Subscribe
    public void locationChangedEvent(Location location) {
        Log.d("eventbuus", "event rcvd");
        mCurrentGeoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
        PokemongoApplication.setmCurrentLoc(mCurrentGeoLocation);
        this.geoQuery = this.geoFire.queryAtLocation(mCurrentGeoLocation, PokemonSharedPreferencesManager.getFloat(getActivity(), Constants.DISTANCE));
        this.geoQuery.addGeoQueryEventListener(this);
        mPosts.clear();
        postKeys.clear();
        mPostAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mPostAdapter);
        EventBus.getDefault().post("stop");
    }

    //called when async task completes
    @Subscribe
    public void loadPostInList(final Post post) {
        mPosts.add(post);
        /*Collections.sort(mPosts, new Comparator<Post>() {
            @Override
            public int compare(Post p1, Post p2) {
                return (post.timeMillis < p2.timeMillis)? 1: -1;
            }
        });*/
        mPostAdapter.notifyDataSetChanged();
        mRecycler.smoothScrollToPosition(mPosts.size());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        //this.geoQuery.addGeoQueryEventListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        //this.geoQuery.removeAllListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //this.geoQuery.removeAllListeners();
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

    @Override
    public void onKeyExited(String key) {
        Log.d("event", key);

        //mPosts
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Log.d("event", key);

    }


    @Override
    public void onGeoQueryError(DatabaseError error) {
        Log.d("event", error.getMessage());
        Toast.makeText(getActivity(), R.string.no_posts_found, Toast.LENGTH_LONG).show();

    }

}
