package pantrypals.models;

import java.util.Map;

/**
 * Created by Hunter Lee on 11/8/2017.
 */

public class Pantry {
    private Map<String, Boolean> items;
    private Map<String, Boolean> ownedBy;
    private Boolean shared;
    protected String databaseID;

    public Pantry() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }


    public Map<String, Boolean> getItems() {
        return items;
    }

    public void setItems(Map<String, Boolean> items) {
        this.items = items;
    }

    public Map<String, Boolean> getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(Map<String, Boolean> ownedBy) {
        this.ownedBy = ownedBy;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public String getDatabaseID() {
        return this.databaseID;
    }

    public void setDatabaseID(String databaseId) {
        this.databaseID = databaseId;
    }

}
