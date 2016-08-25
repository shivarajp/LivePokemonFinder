package com.shivaraj.friendz.shivaraj.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START comment_class]
@IgnoreExtraProperties
public class ChatMessageModel {

    public String uid;
    public String author;
    public String text;
    public String mid;

    public ChatMessageModel() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public ChatMessageModel(String uid, String author, String text, String mid) {
        this.uid = uid;
        this.author = author;
        this.text = text;
        this.mid = mid;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("text", text);
        result.put("mid", mid);
        return result;
    }
    // [END post_to_map]
}
// [END comment_class]
