package pantrypals.models;

import java.util.Map;

/**
 * Created by Hunter Lee on 11/8/2017.
 */

public class Recipe {

    private String title;
    private String text;
    private Map<String, String> timestamp;
    private Map<String, Comment> comments;
    private Map<String, Boolean> likedBy;
    private Map<String, Boolean> postedBy;
    private Map<String, Integer> ratings;
    private float averageRating;

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

    public Map<String, String> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
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

    public Map<String, Integer> getRatings() {
        return ratings;
    }

    public void setRatings(Map<String, Integer> ratings) {
        this.ratings = ratings;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
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

    public Recipe() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }
}
