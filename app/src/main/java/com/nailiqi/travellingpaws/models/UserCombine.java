package com.nailiqi.travellingpaws.models;

public class UserCombine {
    private User user;
    private UserAccount userAccount;

    public UserCombine(User user, UserAccount userAccount) {
        this.user = user;
        this.userAccount = userAccount;
    }

    public User getUser() {
        return user;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public String toString() {
        return "UserCombine{" +
                "user=" + user +
                ", userAccount=" + userAccount +
                '}';
    }
}
