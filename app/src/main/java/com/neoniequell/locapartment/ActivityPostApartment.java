package com.neoniequell.locapartment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;
import com.neoniequell.locapartment.databinding.ActivityPostApartmentBinding;

public class ActivityPostApartment extends AppCompatActivity implements
        View.OnClickListener, DialogAlert.OnDialogAlertDismissListener {

    private byte mCurrentPos = 0;

    private StorageApartmentsImg mStorageApartmentsImg;

    private ModelUser mCurrentUser;

    private ActivityPostApartmentBinding mBinding;
    private Context mContext;

    private AdapterViewPager mAdapter;

    private FragmentSetApartmentDetails mFragApartmentDetails;
    private FragmentSetApartmentImage mFragApartmentImg;

    private DialogLoading mDiaLoading;
    private DialogAlert mDiaAlertFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPostApartmentBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mStorageApartmentsImg = new StorageApartmentsImg(getContentResolver());
        mContext = mBinding.getRoot().getContext();

        mDiaLoading = new DialogLoading(this);
        mDiaAlertFail = new DialogAlert(this);
        mDiaAlertFail.setContent("Post Unsuccessful", "Your apartment was not posted.");

        setActionBar();
        createFragments();
        setAdapter();
        setViewPager();
        setFragmentBundles();
        getCurrentUserFromFirebase();

        mBinding.btnNegative.setOnClickListener(this);
        mBinding.btnPositive.setOnClickListener(this);
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.appBar.toolbar);
        getSupportActionBar().setTitle("Post an Apartment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        mBinding.appBar.toolbar.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(this));

        mBinding.appBar.toolbar.setNavigationOnClickListener(v -> super.onBackPressed());

        //Set status bar color
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
    }

    private void createFragments() {
        mFragApartmentDetails = new FragmentSetApartmentDetails();
        mFragApartmentImg = new FragmentSetApartmentImage();
    }

    private void setAdapter() {
        mAdapter = new AdapterViewPager(getSupportFragmentManager(), getLifecycle());
        mAdapter.add(mFragApartmentDetails);
        mAdapter.add(mFragApartmentImg);
    }

    private void setViewPager() {
        mBinding.viewPager.setAdapter(mAdapter);
        mBinding.viewPager.setUserInputEnabled(false);
        mBinding.viewPager.setCurrentItem(mCurrentPos, false);
    }

    private void setFragmentBundles() {
        //Set step indicators on each fragment
        String[] stepsArr = new String[mAdapter.getItemCount()];
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < stepsArr.length + 1; i++) {
            builder.delete(0, builder.length());
            builder.append("Step ").append(i).append(" of ").append(mAdapter.getItemCount());
            stepsArr[i - 1] = String.valueOf(builder);
        }

        Bundle bundleApartmentDetails = new Bundle();
        bundleApartmentDetails.putString("step", stepsArr[0]);

        Bundle bundleApartmentImg = new Bundle();
        bundleApartmentImg.putString("step", stepsArr[1]);

        mFragApartmentDetails.setArguments(bundleApartmentDetails);
        mFragApartmentImg.setArguments(bundleApartmentImg);
    }

    private void getCurrentUserFromFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        DbUsers dbUsers = new DbUsers();
        dbUsers.getSingleUserInfo(auth.getUid(), user -> mCurrentUser = user);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_positive:
                String text = String.valueOf(mBinding.btnPositive.getText());
                if (text.equalsIgnoreCase("finish")) {
                    mDiaLoading.show();
                    postApartment();
                } else navigateUp();
                break;
            case R.id.btn_negative:
                navigateDown();
                break;
        }
    }

    private void navigateUp() {
        if (isContentComplete()) {
            checkIfFinalStep();

            if (mCurrentPos != mAdapter.getItemCount() - 1) mCurrentPos += 1;

            mBinding.viewPager.setCurrentItem(mCurrentPos);
        }
    }

    private boolean isContentComplete() {
        switch (mCurrentPos) {
            case 0:
                if (mFragApartmentDetails.getTitle().isEmpty()) {
                    mFragApartmentDetails.requestFocusOnTitle();
                    showShortToast("Title cannot be empty");
                    return false;
                } else if (mFragApartmentDetails.getAddress().isEmpty()) {
                    mFragApartmentDetails.requestFocusOnAddress();
                    showShortToast("Address cannot be empty");
                    return false;
                } else if (mFragApartmentDetails.getPrice() == 0.0d) {
                    mFragApartmentDetails.requestFocusOnPrice();
                    showShortToast("Price cannot be empty");
                    return false;
                } else if (mFragApartmentDetails.getDescription().isEmpty()) {
                    mFragApartmentDetails.requestFocusOnDescription();
                    showShortToast("Description cannot be empty");
                    return false;
                }
                break;
            case 1:
                if (mFragApartmentImg.getImageUri() == null) {
                    showShortToast("Please select an image");
                    return false;
                }
                break;
        }

        return true;
    }

    private void checkIfFinalStep() {
        if (mCurrentPos == mAdapter.getItemCount() - 2) {
            mBinding.btnPositive.setText("Finish");
            int bgColor = ContextCompat.getColor(this, android.R.color.holo_green_dark);
            mBinding.btnPositive.setBackgroundColor(bgColor);
        }
    }

    private void navigateDown() {
        if (mCurrentPos != 0) mCurrentPos -= 1;

        //If button text is "submit", revert to "next"
        if (mCurrentPos != mAdapter.getItemCount() - 1) mBinding.btnPositive.setText("Next");

        mBinding.viewPager.setCurrentItem(mCurrentPos, true);
    }

    private void postApartment() {
        mStorageApartmentsImg.setImageUri(mFragApartmentImg.getImageUri());
        mStorageApartmentsImg.setImageReference();
        mStorageApartmentsImg.putImage()
                .addOnSuccessListener(s -> {
                    mStorageApartmentsImg.getImageDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String uriString = String.valueOf(uri);

                                DbApartments dbApartments = new DbApartments();

                                ModelApartment apartment;
                                apartment = setApartmentModel(uriString, dbApartments.getKey());

                                dbApartments.uploadApartment(apartment)
                                        .addOnSuccessListener(s1 -> {
                                            mDiaLoading.dismiss();

                                            DialogAlert diaAlert = new DialogAlert(this, this);
                                            diaAlert.setHeadline("Apartment Posted");
                                            diaAlert.setMessage("Your apartment is now posted.");
                                            diaAlert.delayedShow();
                                        })
                                        .addOnFailureListener(e -> {
                                            e.printStackTrace();
                                            mDiaLoading.dismiss();
                                            mDiaAlertFail.delayedShow();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                mDiaLoading.dismiss();
                                mDiaAlertFail.delayedShow();
                            });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    mDiaLoading.dismiss();
                    mDiaAlertFail.delayedShow();
                });
    }

    private ModelApartment setApartmentModel(String imgUrl, String apartmentKey) {
        ModelApartment apartment = new ModelApartment();

        //Apartment
        apartment.setKey(apartmentKey);
        apartment.setTitle(mFragApartmentDetails.getTitle());
        apartment.setAddress(mFragApartmentDetails.getAddress());
        apartment.setPrice(mFragApartmentDetails.getPrice());
        apartment.setDescription(mFragApartmentDetails.getDescription());
        apartment.setApartmentImg(imgUrl);
        apartment.setAvailability("Available");
        apartment.setImageFileName(mStorageApartmentsImg.getFileName());

        //Landlord
        apartment.setLandlordUid(mCurrentUser.getUid());
        apartment.setLandlordImg(mCurrentUser.getPhotoUrl());
        apartment.setLandlordName(mCurrentUser.getDisplayName());

        return apartment;
    }

    private void showShortToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogAlertDismiss() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == UtilPermission.STORAGE_RQ) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("permission_result", true);
                mFragApartmentImg.setArguments(bundle);
                mFragApartmentImg.checkPermissionResult();
            }
        }
    }
}
