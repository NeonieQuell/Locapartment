package com.neoniequell.locapartment;

import java.util.List;

public class ModelMyProfileParent {

    private String mTitle;
    private List<ModelMyProfileChild> mOptionList;

    public ModelMyProfileParent(String title, List<ModelMyProfileChild> optionList) {
        mTitle = title;
        mOptionList = optionList;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public List<ModelMyProfileChild> getOptionList() {
        return mOptionList;
    }

    public void setOptionList(List<ModelMyProfileChild> optionList) {
        mOptionList = optionList;
    }
}
