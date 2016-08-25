package com.shivaraj.friendz.shivaraj.asynctasks;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shivaraj.friendz.shivaraj.models.ChatMessageModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by SYS on 23-Jul-2016.
 */

public class AsyncMessagesLoader extends AsyncTask<String, ChatMessageModel, ChatMessageModel> {

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected ChatMessageModel doInBackground(String... key) {
        DatabaseReference mDatabaseTemp = FirebaseDatabase.getInstance().getReference("/chat/" + key[0]);
        mDatabaseTemp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatMessageModel msg = dataSnapshot.getValue(ChatMessageModel.class);
                EventBus.getDefault().post(msg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(ChatMessageModel msg) {

    }
}
