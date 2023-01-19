package com.neoniequell.locapartment;

import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class StorageApartmentsImg {

    private String mFileName;

    private StorageReference mStorageRef;
    private StorageReference mFileRef;

    private ContentResolver mContentResolver;

    private Uri mImgUri;

    public StorageApartmentsImg(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
        mStorageRef = FirebaseStorage.getInstance().getReference("userApartmentsImg");
    }

    public void setImageUri(Uri imgUri) {
        mImgUri = imgUri;
    }

    public void setImageReference() {
        mFileName = System.currentTimeMillis() + "." + getFileExtension(mImgUri);
        mFileRef = mStorageRef.child(mFileName);
    }

    public String getFileName() {
        return mFileName;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = mContentResolver;
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public UploadTask putImage() {
        return mFileRef.putFile(mImgUri);
    }

    public Task<Uri> getImageDownloadUrl() {
        return mFileRef.getDownloadUrl();
    }

    public Task<Void> deleteImage(ModelApartment apartment) {
        return mStorageRef.child(apartment.getImageFileName()).delete();
    }
}
