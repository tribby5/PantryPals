package pantrypals.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hunter Lee on 11/7/2017.
 * For now we're only going to use Recipe.java
 */

@Deprecated
@IgnoreExtraProperties
public class Post implements Serializable {

    private String title;
    private String text;
    private Map timestamp;
    private Map<String, Comment> comments;
    private Map<String, Boolean> likedBy;
    private Map<String, Boolean> postedBy;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> generateTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    public Map<String, Comment> getComments() {
        return comments;
    }

    public void setComments(Map<String, Comment> comments) {
        this.comments = comments;
    }

    public Map<String, Boolean> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Map<String, Boolean> likedBy) {
        this.likedBy = likedBy;
    }

    public Map<String, Boolean> getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(Map<String, Boolean> postedBy) {
        this.postedBy = postedBy;
    }

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }
}