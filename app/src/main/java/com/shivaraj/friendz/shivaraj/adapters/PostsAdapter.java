package com.shivaraj.friendz.shivaraj.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.shivaraj.friendz.R;
import com.shivaraj.friendz.shivaraj.activities.PostDetailActivity;
import com.shivaraj.friendz.shivaraj.activities.ShowPostMapActivity;
import com.shivaraj.friendz.shivaraj.models.Post;
import com.shivaraj.friendz.shivaraj.viewholder.PostViewHolder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by SYS on 17-Jul-2016.
 */

public class PostsAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private final Context mContext;
    private final FirebaseAnalytics mFirebaseAnalytics;
    ArrayList<Post> mPosts = new ArrayList<>();
    DatabaseReference mDatabase;
    private int hoursAgo;

    public PostsAdapter(ArrayList<Post> mPosts, Context context) {
        this.mPosts = mPosts;
        this.mContext = context;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);

        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder viewHolder, final int position) {

        final Post post = mPosts.get(position);
        //final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("/posts/"+post.uid);
        //viewHolder.authorView.setText(post.author);
        try {

            Glide.with(mContext).load(post.pimg).placeholder(R.mipmap.ic_launcher).into(viewHolder.postImageIv);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.mapll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, ShowPostMapActivity.class);
                intent.putExtra(ShowPostMapActivity.EXTRA_LOC_KEY, post.loc);
                intent.putExtra(ShowPostMapActivity.EXTRA_TITLE_KEY, post.title);
                mContext.startActivity(intent);
                Bundle bundle = new Bundle();
                bundle.putString("view map list item", "item click");
                mFirebaseAnalytics.logEvent("map list item  click", bundle);
            }
        });

        //viewHolder.postImageIv.setImageDrawable();
        //final String postKey = postRef.getKey();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch PostDetailActivity
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, post.pid);
                mContext.startActivity(intent);
            }
        });


        // Determine if the current user has liked this post and set UI accordingly
        if (post.stars.containsKey(getUid())) {
            viewHolder.starView.setImageResource(R.drawable.pokemon48);
        } else {
            viewHolder.starView.setImageResource(R.drawable.pokemon52);
        }

        viewHolder.authorView.setText(post.author);
        viewHolder.titleView.setText(post.title);
        viewHolder.bodyView.setText(post.body);
        viewHolder.numStarsView.setText(String.valueOf(post.starCount));
        viewHolder.timeTv.setText(getHoursAgo(post.timeMillis));

        viewHolder.pokell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View starView) {
                // Need to write to both places the post is stored
                updatePokes();
                Bundle bundle = new Bundle();
                bundle.putString("poke list item", "poke click");
                mFirebaseAnalytics.logEvent("poke list item  click", bundle);
            }

            private void updatePokes() {
                DatabaseReference globalPostRef = mDatabase.child("posts").child(post.pid);
                DatabaseReference userPostRef = mDatabase.child("user-posts").child(post.uid).child(post.pid);


                //Run transaction and update UI
                globalPostRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Post p = mutableData.getValue(Post.class);
                        if (p == null) {
                            return Transaction.success(mutableData);
                        }

                        if (p.stars.containsKey(getUid())) {
                            // Unstar the post and remove self from stars
                            p.starCount = p.starCount - 1;
                            p.stars.remove(getUid());
                        } else {
                            // Star the post and add self to stars
                            p.starCount = p.starCount + 1;
                            p.stars.put(getUid(), true);
                        }

                        // Set value and report transaction success
                        mutableData.setValue(p);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                        Log.d("PostAdapter", "postTransaction:onComplete:" + databaseError);
                        Log.d("PostAdapter", "postTransaction:onComplete:" + dataSnapshot.getValue());
                        if (databaseError == null && dataSnapshot != null) {
                            Post post = dataSnapshot.getValue(Post.class);
                            viewHolder.numStarsView.setText(String.valueOf(post.starCount));
                            if (post.stars.containsKey(getUid())) {
                                viewHolder.starView.setImageResource(R.drawable.pokemon48);
                            } else {
                                viewHolder.starView.setImageResource(R.drawable.pokemon52);
                            }
                        }
                    }
                });


                // Star clicked just update user posts as well
                onStarClicked(userPostRef);
            }

        });

        // Bind Post to ViewHolder, setting OnClickListener for the star button
      /*  viewHolder.bindToPost(post, new View.OnClickListener() {
            @Override
            public void onClick(View starView) {
                // Need to write to both places the post is stored
                DatabaseReference globalPostRef = mDatabase.child("posts").child(post.pid);
                DatabaseReference userPostRef = mDatabase.child("user-posts").child(post.uid).child(post.pid);

                // Run two transactions
                onStarClicked(globalPostRef);
                onStarClicked(userPostRef);
            }
        });*/
    }


    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("PostAdapter", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public String getHoursAgo(long timeMillis) {

        long millis = System.currentTimeMillis() - timeMillis;
        return (TimeUnit.MILLISECONDS.toHours(millis)) <= 0 ? TimeUnit.MILLISECONDS.toMinutes(millis) + "min's sgo" : TimeUnit.MILLISECONDS.toHours(millis) + "hour's ago";
    }
}
