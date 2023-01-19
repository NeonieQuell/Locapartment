package com.neoniequell.locapartment;

public class ModelMessage {

    private String mConvoKey;
    private String mKey;
    private String mSender;
    private String mReceiver;
    private String mMessage;

    public ModelMessage() {

    }

    public ModelMessage(String convoKey, String key, String sender,
                        String receiver, String message) {
        mConvoKey = convoKey;
        mKey = key;
        mSender = sender;
        mReceiver = receiver;
        mMessage = message;
    }

    public String getConvoKey() {
        return mConvoKey;
    }

    public void setConvoKey(String convoKey) {
        mConvoKey = convoKey;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String sender) {
        mSender = sender;
    }

    public String getReceiver() {
        return mReceiver;
    }

    public void setReceiver(String receiver) {
        mReceiver = receiver;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }
}
