package com.shivaraj.friendz.shivaraj.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public String uid;
    public String author;
    public String title;
    public String body;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
    public String loc;
    public String pid;
    public long timeMillis;
    public String pimg;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String title, String body, String loc, String pid, long timeMillis, String pimg) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.loc = loc;
        this.pid = pid;
        this.timeMillis = timeMillis;
        this.pimg = pimg;

    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("loc", loc);
        result.put("pid", pid);
        result.put("timeMillis", timeMillis);
        result.put("pimg", pimg);
        return result;
    }
    // [END post_to_map]

}
// [END post_class]
