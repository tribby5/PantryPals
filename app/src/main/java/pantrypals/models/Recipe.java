package pantrypals.models;

import com.google.firebase.database.ServerValue;

import java.util.List;
import java.util.Map;

/**
 * Created by Hunter Lee on 11/8/2017.
 */

public class Recipe {

    private String title;
    private String text;
    private String caption;

    private Map<String, Comment> comments;
    private Map<String, Boolean> likedBy;
    private Map<String, Boolean> postedBy;
    private Map<String, Integer> ratings;



    private List<Ingredient> requiredIngredients;


    private double averageRating;
    private String dbKey;


    public List<Ingredient> getRequiredIngredients() {
        return requiredIngredients;
    }

    public void setRequiredIngredients(List<Ingredient> requiredIngredients) {
        this.requiredIngredients = requiredIngredients;
    }

    public String getDbKey() {
        return dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }


    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }


    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    private String imgURL;


//    public void setTimestamp(Map timestamp) {
//        this.timestamp = timestamp;
//    }

//    private Map timestamp;

    // Just using string for timeposted. Make this randomly generated.
    private String timePosted;

    public String getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(String timePosted) {
        this.timePosted = timePosted;
    }





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


    public Map<String, String> generateTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public double getAverageRating() {
        return averageRating;
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

    public static class Ingredient {
        public Ingredient() {
        }

        private String name;
        private double amount;
        private String unit;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}
