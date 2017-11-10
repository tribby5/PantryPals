package pantrypals.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

/**
 * Created by Hunter Lee on 11/7/2017.
 */
@IgnoreExtraProperties
public class User {

    private String name;
    private String email;
    private String bio;
    private Map<String, Boolean> followers;
    private Map<String, Boolean> following;
    private Map<String, Boolean> groups;
    private Map<String, Boolean> likedPosts;
    private Map<String, Boolean> likedRecipes;
    private Map<String, Boolean> savedPosts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Map<String, Boolean> getFollowers() {
        return followers;
    }

    public void setFollowers(Map<String, Boolean> followers) {
        this.followers = followers;
    }

    public Map<String, Boolean> getFollowing() {
        return following;
    }

    public void setFollowing(Map<String, Boolean> following) {
        this.following = following;
    }

    public Map<String, Boolean> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, Boolean> groups) {
        this.groups = groups;
    }

    public Map<String, Boolean> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(Map<String, Boolean> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public Map<String, Boolean> getLikedRecipes() {
        return likedRecipes;
    }

    public void setLikedRecipes(Map<String, Boolean> likedRecipes) {
        this.likedRecipes = likedRecipes;
    }

    public Map<String, Boolean> getSavedPosts() {
        return savedPosts;
    }

    public void setSavedPosts(Map<String, Boolean> savedPosts) {
        this.savedPosts = savedPosts;
    }

    public Map<String, Boolean> getPantries() {
        return pantries;
    }

    public void setPantries(Map<String, Boolean> pantries) {
        this.pantries = pantries;
    }

    public Map<String, Boolean> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<String, Boolean> preferences) {
        this.preferences = preferences;
    }

    public Map<String, Boolean> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Map<String, Boolean> restrictions) {
        this.restrictions = restrictions;
    }

    public Map<String, Boolean> pantries;
    public Map<String, Boolean> preferences;
    public Map<String, Boolean> restrictions;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
}