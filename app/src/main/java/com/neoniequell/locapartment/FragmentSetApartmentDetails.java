package com.neoniequell.locapartment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.neoniequell.locapartment.databinding.FragmentSetApartmentDetailsBinding;

public class FragmentSetApartmentDetails extends Fragment {

    private String mStep;
    private boolean mIsForEdit;

    private ModelApartment mApartment;

    private FragmentSetApartmentDetailsBinding mBinding;
    private Context mContext;

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
        mBinding = FragmentSetApartmentDetailsBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        checkIfForEdit();

        mBinding.tvStepIndicator.setText(mStep);

        return mBinding.getRoot();
    }

    public String getTitle() {
        return String.valueOf(mBinding.etTitle.getText()).trim();
    }

    public String getAddress() {
        return String.valueOf(mBinding.etAddress.getText()).trim();
    }

    public double getPrice() {
        String strPrice = String.valueOf(mBinding.etPrice.getText()).trim();

        if (strPrice.isEmpty()) return 0.0d;
        else return Double.parseDouble(strPrice);
    }

    public String getDescription() {
        return String.valueOf(mBinding.etDescription.getText()).trim();
    }

    public void requestFocusOnTitle() {
        mBinding.etTitle.requestFocus();
    }

    public void requestFocusOnAddress() {
        mBinding.etAddress.requestFocus();
    }

    public void requestFocusOnPrice() {
        mBinding.etPrice.requestFocus();
    }

    public void requestFocusOnDescription() {
        mBinding.etDescription.requestFocus();
    }

    private void checkIfForEdit() {
        if (mIsForEdit) {
            mBinding.etTitle.setText(mApartment.getTitle());
            mBinding.etAddress.setText(mApartment.getAddress());
            mBinding.etDescription.setText(mApartment.getDescription());

            double price = mApartment.getPrice();
            mBinding.etPrice.setText(String.valueOf(price));
        }
    }
}
