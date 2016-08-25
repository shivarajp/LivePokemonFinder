package com.shivaraj.friendz.shivaraj.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MapPostsFragment extends PostListFragment {

    public MapPostsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("user-posts")
                .child(getUid());
    }
}
