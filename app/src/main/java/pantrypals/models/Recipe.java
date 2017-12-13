package pantrypals.models;

import com.google.firebase.database.ServerValue;

import java.util.List;
import java.util.Map;

/**
 * Created by Hunter Lee on 11/8/2017.
 */

public class Recipe {

    private String name;
    private String text;
    private String caption;

    private Map<String, Comment> comments;
    private Map<String, Boolean> likedBy;
    private Map<String, Boolean> postedBy;

    // following groupId is null if it's public recipe
    private String groupId;
    private Map<String, Integer> ratings;

    private List<Ingredient> ingredients;
    private List<String> instructions;
    private List<String> tags;

    private String averageRating;
    private String dbKey;
    private String timePosted;
    private String imageURL;
    private long negTimestamp;


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getNegTimestamp() {
        return negTimestamp;
    }

    public void setNegTimestamp(long negTimestamp) {
        this.negTimestamp = negTimestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public Map<String, Integer> getRatings() {
        return ratings;
    }

    public void setRatings(Map<String, Integer> ratings) {
        this.ratings = ratings;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public String getDbKey() {
        return dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }

    public String getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(String timePosted) {
        this.timePosted = timePosted;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }




//    public void setTimestamp(Map timestamp) {
//        this.timestamp = timestamp;
//    }

//    private Map timestamp;

    // Just using string for timeposted. Make this randomly generated.

    public Map<String, String> generateTimestamp() {
        return ServerValue.TIMESTAMP;
    }

//    public void setAverageRating(double averageRating) {
//        this.averageRating = averageRating;
//    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
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
