package com.nailiqi.travellingpaws.models;

public class UserAccount {

    private String description;
    private long followers;
    private long following;
    private long posts;
    private String profile_image;
    private String username;

    public UserAccount(String description, long followers, long following,
                               long posts, String profile_image, String username) {
        this.description = description;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
        this.profile_image = profile_image;
        this.username = username;
    }
    public UserAccount() {

    }

    public String getDescription() {
        return description;
    }

    public void setDscription(String description) {
        this.description = description;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "description='" + description + '\'' +
                               ", followers=" + followers +
                ", following=" + following +
                ", posts=" + posts +
                ", profile_image='" + profile_image + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
