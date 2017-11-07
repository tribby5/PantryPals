package pantrypals.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Hunter Lee on 11/7/2017.
 */
@IgnoreExtraProperties
public class User {

    public String name;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

}