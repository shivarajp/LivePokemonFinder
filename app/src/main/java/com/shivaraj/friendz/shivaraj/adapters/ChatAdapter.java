package com.shivaraj.friendz.shivaraj.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shivaraj.friendz.R;
import com.shivaraj.friendz.shivaraj.models.ChatMessageModel;
import com.shivaraj.friendz.shivaraj.viewholder.ChatViewHolder;

import java.util.ArrayList;

/**
 * Created by SYS on 19-Jul-2016.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private Context mContext;
    ArrayList<ChatMessageModel> mMessages = new ArrayList<>();
    DatabaseReference mDatabase;

    public ChatAdapter(Context mContext, ArrayList<ChatMessageModel> mMessages) {
        this.mContext = mContext;
        this.mMessages = mMessages;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_msg, parent, false);

        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatMessageModel msg = mMessages.get(position);

        if (msg.uid.equals(getUid())) {
            //My message bubble
            holder.chatNameTv.setText(msg.author);
            holder.chatNameTv.setTextColor(Color.parseColor("#039BE5"));
            holder.chatMsgTv.setText(msg.text);
            holder.chatBubblell.setGravity(Gravity.RIGHT);
        } else {
            //Others Message bubble
            holder.chatNameTv.setText(msg.author);
            holder.chatNameTv.setTextColor(Color.parseColor("#b20000"));
            holder.chatNameTv.setText(msg.text);
            holder.chatBubblell.setGravity(Gravity.LEFT);
        }
    }


    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public String getUid() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid != null) {
            return uid;
        } else {
            return "";
        }
    }
}
