package pantrypals.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by adityasrinivasan on 12/7/2017.
 */

public class Group implements Serializable {
    private Map<String, Boolean> members;
    private Map<String, Boolean> recipes;
    private String name;
    private String category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Group() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }

    public Map<String, Boolean> getRecipes() {
        return recipes;
    }

    public void setRecipes(Map<String, Boolean> recipes) {
        this.recipes = recipes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
