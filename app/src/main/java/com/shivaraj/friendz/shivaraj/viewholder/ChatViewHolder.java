package com.shivaraj.friendz.shivaraj.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shivaraj.friendz.R;

/**
 * Created by SYS on 19-Jul-2016.
 */

public class ChatViewHolder extends RecyclerView.ViewHolder {

    public TextView chatNameTv;
    public TextView chatMsgTv;
    public LinearLayout chatBubblell;

    public ChatViewHolder(View itemView) {
        super(itemView);
        chatNameTv = (TextView) itemView.findViewById(R.id.post_author);
        chatMsgTv = (TextView) itemView.findViewById(R.id.chatMsgTv);
        chatBubblell = (LinearLayout) itemView.findViewById(R.id.chatBubblell);
    }
}
