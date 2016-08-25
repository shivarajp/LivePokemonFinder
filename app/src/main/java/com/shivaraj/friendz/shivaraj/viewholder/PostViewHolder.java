package com.shivaraj.friendz.shivaraj.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shivaraj.friendz.R;
import com.shivaraj.friendz.shivaraj.models.Post;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public ImageView postImageIv;
    public TextView numStarsView;
    public TextView bodyView;
    public TextView timeTv;
    public LinearLayout pokell;
    public LinearLayout comentll;
    public LinearLayout mapll;


    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.post_title);
        timeTv = (TextView) itemView.findViewById(R.id.post_time);
        authorView = (TextView) itemView.findViewById(R.id.post_author);
        starView = (ImageView) itemView.findViewById(R.id.star);
        postImageIv = (ImageView) itemView.findViewById(R.id.post_imgIv);
        numStarsView = (TextView) itemView.findViewById(R.id.post_numstars);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        pokell = (LinearLayout)itemView.findViewById(R.id.pokell);
        comentll = (LinearLayout)itemView.findViewById(R.id.commentll);
        mapll = (LinearLayout)itemView.findViewById(R.id.mapll);
    }

    public void bindToPost(Post post, View.OnClickListener starClickListener) {
        titleView.setText(post.title);
        authorView.setText(post.author);
        numStarsView.setText(String.valueOf(post.starCount));
        bodyView.setText(post.body);
        starView.setOnClickListener(starClickListener);
    }
}
