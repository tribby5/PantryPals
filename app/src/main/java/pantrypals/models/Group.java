package pantrypals.models;

import java.util.List;
import java.util.Map;

/**
 * Created by adityasrinivasan on 12/7/2017.
 */

public class Group {
    private Map<String, Boolean> members;
    private Map<String, Boolean> recipes;
    private String name;

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
}
