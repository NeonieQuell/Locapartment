package com.neoniequell.locapartment;

import java.util.List;

public class ModelConversation {

    private String mKey;
    private List<String> mParticipantList;

    public ModelConversation() {
        
    }

    public ModelConversation(String key, List<String> participantList) {
        mKey = key;
        mParticipantList = participantList;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public List<String> getParticipantList() {
        return mParticipantList;
    }

    public void setParticipantList(List<String> participantList) {
        mParticipantList = participantList;
    }
}
