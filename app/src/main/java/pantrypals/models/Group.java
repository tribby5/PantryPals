package pantrypals.models;

import java.util.Map;

/**
 * Created by Hunter Lee on 11/8/2017.
 */

public class Group {
    private Map<String, Boolean> members;
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
}
