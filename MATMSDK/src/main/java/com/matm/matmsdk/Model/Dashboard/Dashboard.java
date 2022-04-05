package com.matm.matmsdk.Model.Dashboard;

public class Dashboard {
    private UserInfo userInfo;

    public UserInfo getUserInfo ()
    {
        return userInfo;
    }

    public void setUserInfo (UserInfo userInfo)
    {
        this.userInfo = userInfo;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [userInfo = "+userInfo+"]";
    }


}
