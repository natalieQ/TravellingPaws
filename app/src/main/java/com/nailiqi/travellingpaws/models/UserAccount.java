package com.nailiqi.travellingpaws.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserAccount implements Parcelable{

    private String user_id;
    private String description;
    private long followers;
    private long following;
    private long posts;
    private String profile_image;
    private String username;
    private String petname;


    public UserAccount(String user_id, String description, long followers, long following, long posts, String profile_image, String username, String petname) {
        this.user_id = user_id;
        this.description = description;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
        this.profile_image = profile_image;
        this.username = username;
        this.petname = petname;
    }

    public UserAccount() {

    }

    protected UserAccount(Parcel in) {
        user_id = in.readString();
        description = in.readString();
        followers = in.readLong();
        following = in.readLong();
        posts = in.readLong();
        profile_image = in.readString();
        username = in.readString();
        petname = in.readString();
    }

    public static final Creator<UserAccount> CREATOR = new Creator<UserAccount>() {
        @Override
        public UserAccount createFromParcel(Parcel in) {
            return new UserAccount(in);
        }

        @Override
        public UserAccount[] newArray(int size) {
            return new UserAccount[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
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

    public String getPetname() {
        return petname;
    }

    public void setPetname(String petname) {
        this.petname = petname;
    }


    @Override
    public String toString() {
        return "UserAccount{" +
                "description='" + description + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", posts=" + posts +
                ", profile_image='" + profile_image + '\'' +
                ", username='" + username + '\'' +
                ", petname='" + petname + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(user_id);
        dest.writeString(description);
        dest.writeLong(followers);
        dest.writeLong(following);
        dest.writeLong(posts);
        dest.writeString(profile_image);
        dest.writeString(username);
        dest.writeString(petname);

    }


}
