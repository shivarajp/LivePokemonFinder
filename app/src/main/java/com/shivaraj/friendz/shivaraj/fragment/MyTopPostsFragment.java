package com.shivaraj.friendz.shivaraj.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shivaraj.friendz.R;
import com.shivaraj.friendz.shivaraj.adapters.PostsAdapter;
import com.shivaraj.friendz.shivaraj.models.Post;

import java.util.ArrayList;

public class MyTopPostsFragment extends Fragment {

    private PostsAdapter mPostAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private ArrayList<Post> mPosts = new ArrayList<>();
    private DatabaseReference mDatabase;

    public MyTopPostsFragment() {
    }
    //Query myTopPostsQuery = databaseReference.child("user-posts").child(myUserId)

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_posts_around, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference("/user-posts/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        Query query = mDatabase.limitToFirst(100);

        mRecycler = (RecyclerView) view.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(mManager);

        mPostAdapter = new PostsAdapter(mPosts, getActivity());
        mRecycler.setAdapter(mPostAdapter);

        if (mPosts.size() <= 0) {

            mDatabase.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Post post = dataSnapshot.getValue(Post.class);
                    mPosts.add(post);
                    mPostAdapter.notifyDataSetChanged();
                    mRecycler.smoothScrollToPosition(mPosts.size());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
