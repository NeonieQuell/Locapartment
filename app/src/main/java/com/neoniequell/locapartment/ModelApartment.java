package com.neoniequell.locapartment;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ModelApartment implements Parcelable {

    //Landlord
    private String mLandlordUid;
    private String mLandlordImg;
    private String mLandlordName;

    //Apartment
    private String mKey;
    private String mApartmentImg;
    private double mPrice;
    private String mTitle;
    private String mAddress;
    private String mDescription;
    private String mAvailability;
    private String mImageFileName;

    public ModelApartment() {

    }

    public ModelApartment(String landlordUid, String landlordImg, String landlordName,
                          String key, String apartmentImg, double price, String title,
                          String address, String description, String availability,
                          String imageFileName) {
        mLandlordUid = landlordUid;
        mLandlordImg = landlordImg;
        mKey = key;
        mLandlordName = landlordName;
        mApartmentImg = apartmentImg;
        mPrice = price;
        mTitle = title;
        mAddress = address;
        mDescription = description;
        mAvailability = availability;
        mImageFileName = imageFileName;
    }

    protected ModelApartment(Parcel in) {
        mLandlordUid = in.readString();
        mLandlordImg = in.readString();
        mLandlordName = in.readString();
        mKey = in.readString();
        mApartmentImg = in.readString();
        mPrice = in.readDouble();
        mTitle = in.readString();
        mAddress = in.readString();
        mDescription = in.readString();
        mAvailability = in.readString();
        mImageFileName = in.readString();
    }

    public static final Creator<ModelApartment> CREATOR = new Creator<ModelApartment>() {
        @Override
        public ModelApartment createFromParcel(Parcel in) {
            return new ModelApartment(in);
        }

        @Override
        public ModelApartment[] newArray(int size) {
            return new ModelApartment[size];
        }
    };

    public String getLandlordUid() {
        return mLandlordUid;
    }

    public void setLandlordUid(String landlordUid) {
        mLandlordUid = landlordUid;
    }

    public String getLandlordImg() {
        return mLandlordImg;
    }

    public void setLandlordImg(String landlordImg) {
        mLandlordImg = landlordImg;
    }

    public String getLandlordName() {
        return mLandlordName;
    }

    public void setLandlordName(String landlordName) {
        mLandlordName = landlordName;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getApartmentImg() {
        return mApartmentImg;
    }

    public void setApartmentImg(String apartmentImg) {
        mApartmentImg = apartmentImg;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        mPrice = price;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getAvailability() {
        return mAvailability;
    }

    public void setAvailability(String availability) {
        mAvailability = availability;
    }

    public String getImageFileName() {
        return mImageFileName;
    }

    public void setImageFileName(String imageFileName) {
        mImageFileName = imageFileName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mLandlordUid);
        dest.writeString(mLandlordImg);
        dest.writeString(mLandlordName);
        dest.writeString(mKey);
        dest.writeString(mApartmentImg);
        dest.writeDouble(mPrice);
        dest.writeString(mTitle);
        dest.writeString(mAddress);
        dest.writeString(mDescription);
        dest.writeString(mAvailability);
        dest.writeString(mImageFileName);
    }
}
