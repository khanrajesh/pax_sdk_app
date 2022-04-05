package com.matm.matmsdk.Model.Dashboard;

public class UserInfo {
    private String adminName;

    private String mposNumber;

    private String userType;

    private String userBalance;

    private String promotionalMessage;

    private String userName;

    private UserProfile userProfile;

    private String userBrand;

    private UserFeature[] userFeature;

    public String getAdminName ()
    {
        return adminName;
    }

    public void setAdminName (String adminName)
    {
        this.adminName = adminName;
    }

    public String getMposNumber ()
    {
        return mposNumber;
    }

    public void setMposNumber (String mposNumber)
    {
        this.mposNumber = mposNumber;
    }

    public String getUserType ()
    {
        return userType;
    }

    public void setUserType (String userType)
    {
        this.userType = userType;
    }

    public String getUserBalance ()
    {
        return userBalance;
    }

    public void setUserBalance (String userBalance)
    {
        this.userBalance = userBalance;
    }

    public String getPromotionalMessage ()
    {
        return promotionalMessage;
    }

    public void setPromotionalMessage (String promotionalMessage)
    {
        this.promotionalMessage = promotionalMessage;
    }

    public String getUserName ()
    {
        return userName;
    }

    public void setUserName (String userName)
    {
        this.userName = userName;
    }

    public UserProfile getUserProfile ()
    {
        return userProfile;
    }

    public void setUserProfile (UserProfile userProfile)
    {
        this.userProfile = userProfile;
    }

    public String getUserBrand ()
    {
        return userBrand;
    }

    public void setUserBrand (String userBrand)
    {
        this.userBrand = userBrand;
    }

    public UserFeature[] getUserFeature ()
    {
        return userFeature;
    }

    public void setUserFeature (UserFeature[] userFeature)
    {
        this.userFeature = userFeature;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [adminName = "+adminName+", mposNumber = "+mposNumber+", userType = "+userType+", userBalance = "+userBalance+", promotionalMessage = "+promotionalMessage+", userName = "+userName+", userProfile = "+userProfile+", userBrand = "+userBrand+", userFeature = "+userFeature+"]";
    }
}
