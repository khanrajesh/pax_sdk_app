package com.matm.matmsdk.Model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("username")
    private String userName;

    @SerializedName("password")
    private String password;

    public User(String user, String password) {
        this.userName = user;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
