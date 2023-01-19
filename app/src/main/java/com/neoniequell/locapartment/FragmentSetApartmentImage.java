package com.neoniequell.locapartment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.neoniequell.locapartment.databinding.FragmentSetApartmentImageBinding;

public class FragmentSetApartmentImage extends Fragment {

    private String mStep;
    private boolean mIsForEdit;

    private ModelApartment mApartment;

    private FragmentSetApartmentImageBinding mBinding;
    private Context mContext;

    private Uri mImageUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mStep = bundle.getString("step");
            mIsForEdit = bundle.getBoolean("edit");
            mApartment = bundle.getParcelable("apartment");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSetApartmentImageBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        mBinding.tvStepIndicator.setText(mStep);

        if (mImageUri != null) Glide.with(mContext).load(mImageUri).into(mBinding.apartmentImg);

        checkIfForEdit();

        mBinding.btnSelectImg.setOnClickListener(v -> selectImage());

        return mBinding.getRoot();
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    private void selectImage() {
        if (UtilPermission.hasStoragePermission(mContext)) {
            if (activityResult != null) activityResult.launch("image/*");
        } else UtilPermission.requestStoragePermission(requireActivity());
    }

    private ActivityResultLauncher<String> activityResult =
            registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    mImageUri = result;
                    Glide.with(mContext).load(mImageUri).into(mBinding.apartmentImg);
                }
            });

    public void checkPermissionResult() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            boolean result = bundle.getBoolean("permission_result");
            if (result) selectImage();
        }
    }

    private void checkIfForEdit() {
        if (mIsForEdit) {
            Glide.with(mContext).load(mApartment.getApartmentImg()).into(mBinding.apartmentImg);
            mBinding.btnSelectImg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activityResult = null;
    }
}
