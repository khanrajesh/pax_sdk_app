package com.matm.matmsdk.Model.Dashboard;

public class UserFeature {
    private String featureName;

    private String active;

    private String id;

    public String getFeatureName ()
    {
        return featureName;
    }

    public void setFeatureName (String featureName)
    {
        this.featureName = featureName;
    }

    public String getActive ()
    {
        return active;
    }

    public void setActive (String active)
    {
        this.active = active;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [featureName = "+featureName+", active = "+active+", id = "+id+"]";
    }


}
