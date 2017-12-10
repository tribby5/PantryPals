package pantrypals.util;

import pantrypals.models.User;

/**
 * Created by adityasrinivasan on 09/12/17.
 */

public class AuthUserInfo {

    public static final AuthUserInfo INSTANCE = new AuthUserInfo();

    private User user;

    private AuthUserInfo() {
        // Singleton
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
