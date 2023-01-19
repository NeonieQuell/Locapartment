package com.neoniequell.locapartment;

public class ModelOption {

    private String mName;
    private int mIcon;

    public ModelOption() {

    }

    public ModelOption(String name, int icon) {
        mName = name;
        mIcon = icon;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int icon) {
        mIcon = icon;
    }
}
