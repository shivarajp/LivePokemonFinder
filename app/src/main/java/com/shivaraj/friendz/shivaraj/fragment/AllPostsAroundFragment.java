package com.shivaraj.friendz.shivaraj.fragment;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shivaraj.friendz.R;
import com.shivaraj.friendz.shivaraj.PokemongoApplication;
import com.shivaraj.friendz.shivaraj.adapters.PostsAdapter;
import com.shivaraj.friendz.shivaraj.asynctasks.AsyncAllPostsLoader;
import com.shivaraj.friendz.shivaraj.geofire.GeoFire;
import com.shivaraj.friendz.shivaraj.geofire.GeoLocation;
import com.shivaraj.friendz.shivaraj.geofire.GeoQuery;
import com.shivaraj.friendz.shivaraj.geofire.GeoQueryEventListener;
import com.shivaraj.friendz.shivaraj.models.Post;
import com.shivaraj.friendz.shivaraj.models.Post2;
import com.shivaraj.friendz.shivaraj.utils.Constants;
import com.shivaraj.friendz.shivaraj.utils.PokemonSharedPreferencesManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;


public class AllPostsAroundFragment extends Fragment implements GeoQueryEventListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private static final GeoLocation INITIAL_CENTER = new GeoLocation(37.7749, 122.4194);
    public GeoLocation mCurrentGeoLocation = new GeoLocation(37.7749, 122.4194);
    private PostsAdapter mPostAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private ArrayList<Post> mPosts = new ArrayList<>();
    private DatabaseReference mDatabase;
    private ArrayList<String> postKeys = new ArrayList<String>();


    private OnFragmentInteractionListener mListener;

    public AllPostsAroundFragment() {
        // Required empty public constructor
    }

    public static AllPostsAroundFragment newInstance(String param1, String param2) {
        AllPostsAroundFragment fragment = new AllPostsAroundFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_posts_around, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]
        // setup GeoFire
        this.geoFire = new GeoFire(mDatabase.child("/geofire"));
        // radius in km
        this.geoQuery = this.geoFire.queryAtLocation(INITIAL_CENTER, PokemonSharedPreferencesManager.getFloat(getActivity(), Constants.DISTANCE));

        mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(mManager);

        mPostAdapter = new PostsAdapter(mPosts, getActivity());
        mRecycler.setAdapter(mPostAdapter);

        // Inflate the layout for this fragment
        return rootView;
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

    @Subscribe
    public void loadNewPost(Post2 post2) {
        Post post = new Post();
        post.pid = post2.pid;
        post.pimg = post2.pimg;
        post.pid = post2.pid;
        post.timeMillis = post2.timeMillis;
        post.author = post2.author;
        post.body = post2.body;
        post.loc = post2.loc;
        post.title = post2.title;
        post.starCount = post2.starCount;
        post.stars = post2.stars;
        post.uid = post2.uid;

        mPosts.add(post);
        mPostAdapter.notifyDataSetChanged();
        mRecycler.smoothScrollToPosition(mPosts.size());
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        Log.d("geoevent", key);
        postKeys.add(key);

        if (postKeys.size() < 30) {
            AsyncAllPostsLoader asyncAllPostsLoader = new AsyncAllPostsLoader();
            asyncAllPostsLoader.execute(key);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // EventBus.getDefault().register(this);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
