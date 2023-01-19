package com.neoniequell.locapartment;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ModelUser implements Parcelable {

    private String mUid;
    private String mDisplayName;
    private String mEmail;
    private String mPhotoUrl;

    public ModelUser() {

    }

    public ModelUser(String uid, String displayName, String email, String photoUrl) {
        mUid = uid;
        mDisplayName = displayName;
        mEmail = email;
        mPhotoUrl = photoUrl;
    }

    protected ModelUser(Parcel in) {
        mUid = in.readString();
        mDisplayName = in.readString();
        mEmail = in.readString();
        mPhotoUrl = in.readString();
    }

    public static final Creator<ModelUser> CREATOR = new Creator<ModelUser>() {
        @Override
        public ModelUser createFromParcel(Parcel in) {
            return new ModelUser(in);
        }

        @Override
        public ModelUser[] newArray(int size) {
            return new ModelUser[size];
        }
    };

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        mPhotoUrl = photoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mUid);
        dest.writeString(mDisplayName);
        dest.writeString(mEmail);
        dest.writeString(mPhotoUrl);
    }
}
