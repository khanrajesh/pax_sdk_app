package com.matm.matmsdk.Model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {


    @SerializedName("token")
    private String token;
    @SerializedName("admin")
    private String admin;


    public LoginResponse(String token, String admin) {
        this.token = token;
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", admin='" + admin + '\'' +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

}
