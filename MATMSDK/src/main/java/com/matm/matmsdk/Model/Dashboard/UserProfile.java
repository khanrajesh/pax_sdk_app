package com.matm.matmsdk.Model.Dashboard;

public class UserProfile {
    private String firstName;

    private String lastName;

    private String address;

    private String panCard;

    private String city;

    private String mobileNumber;

    private String shopName;

    private String userType;

    private String state;

    private String email;

    private String adharCard;

    public String getFirstName ()
    {
        return firstName;
    }

    public void setFirstName (String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName ()
    {
        return lastName;
    }

    public void setLastName (String lastName)
    {
        this.lastName = lastName;
    }

    public String getAddress ()
    {
        return address;
    }

    public void setAddress (String address)
    {
        this.address = address;
    }

    public String getPanCard ()
    {
        return panCard;
    }

    public void setPanCard (String panCard)
    {
        this.panCard = panCard;
    }

    public String getCity ()
    {
        return city;
    }

    public void setCity (String city)
    {
        this.city = city;
    }

    public String getMobileNumber ()
    {
        return mobileNumber;
    }

    public void setMobileNumber (String mobileNumber)
    {
        this.mobileNumber = mobileNumber;
    }

    public String getShopName ()
    {
        return shopName;
    }

    public void setShopName (String shopName)
    {
        this.shopName = shopName;
    }

    public String getUserType ()
    {
        return userType;
    }

    public void setUserType (String userType)
    {
        this.userType = userType;
    }

    public String getState ()
    {
        return state;
    }

    public void setState (String state)
    {
        this.state = state;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    public String getAdharCard ()
    {
        return adharCard;
    }

    public void setAdharCard (String adharCard)
    {
        this.adharCard = adharCard;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [firstName = "+firstName+", lastName = "+lastName+", address = "+address+", panCard = "+panCard+", city = "+city+", mobileNumber = "+mobileNumber+", shopName = "+shopName+", userType = "+userType+", state = "+state+", email = "+email+", adharCard = "+adharCard+"]";
    }
}
