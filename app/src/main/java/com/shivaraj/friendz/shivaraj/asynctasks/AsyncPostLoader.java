package com.shivaraj.friendz.shivaraj.asynctasks;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivaraj.friendz.shivaraj.models.Post;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by SYS on 23-Jul-2016.
 */

public class AsyncPostLoader extends AsyncTask<String, Post, Post> {

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Post doInBackground(String... key) {
        DatabaseReference mDatabaseTemp = FirebaseDatabase.getInstance().getReference("/posts/" + key[0]);
        mDatabaseTemp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                EventBus.getDefault().post(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(Post posts) {

    }
}
