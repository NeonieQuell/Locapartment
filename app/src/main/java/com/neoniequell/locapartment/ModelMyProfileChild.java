package com.neoniequell.locapartment;

public class ModelMyProfileChild {

    private int mIcon;
    private String mName;

    public ModelMyProfileChild(int icon, String name) {
        mIcon = icon;
        mName = name;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int icon) {
        mIcon = icon;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
