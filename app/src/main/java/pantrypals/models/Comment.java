package pantrypals.models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.Map;

/**
 * Created by Hunter Lee on 11/7/2017.
 */

@IgnoreExtraProperties
public class Comment {
    private String author;
    private String text;
    private Long timestamp;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> generateTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Comment(String uid, String author, String text) {
        this.author = author;
        this.text = text;
    }

}