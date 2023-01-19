package com.neoniequell.locapartment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.neoniequell.locapartment.databinding.ActivityViewApartmentBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivityViewApartment extends AppCompatActivity implements
        DialogOptions.OnDialogOptionClickListener, DialogConfirm.OnPositiveButtonClickListener {

    private FirebaseAuth mAuth;

    private List<ModelOption> mOptionList;
    private ModelApartment mApartment;

    private ActivityViewApartmentBinding mBinding;

    private DialogOptions mDiaOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityViewApartmentBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mApartment = getIntent().getParcelableExtra("apartment");
        mAuth = FirebaseAuth.getInstance();

        setFullScreen();
        checkCurrentUser();
        setApartment();
        setApartmentAvailabilityIcon();
        setDialogOptions();

        mBinding.fabBack.setOnClickListener(v -> super.onBackPressed());
        mBinding.fabOption.setOnClickListener(v -> mDiaOptions.show());
        mBinding.btnChat.setOnClickListener(v -> chatLandlord());
    }

    private void setFullScreen() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.layoutParent, (v, insets) -> {
            ViewGroup.MarginLayoutParams fabBackParams = (ViewGroup.MarginLayoutParams)
                    mBinding.fabBack.getLayoutParams();

            ViewGroup.MarginLayoutParams fabMoreParams = (ViewGroup.MarginLayoutParams)
                    mBinding.fabOption.getLayoutParams();

            fabBackParams.setMargins(0, insets.getSystemWindowInsetTop(), 0, 0);
            fabMoreParams.setMargins(0, insets.getSystemWindowInsetTop(), 0, 0);

            mBinding.fabBack.setLayoutParams(fabBackParams);
            mBinding.fabOption.setLayoutParams(fabMoreParams);

            insets.consumeSystemWindowInsets();
            return insets;
        });
    }

    private void checkCurrentUser() {
        if (Objects.equals(mAuth.getUid(), mApartment.getLandlordUid())) {
            mBinding.fabOption.setVisibility(View.VISIBLE);
            mBinding.btnChat.setVisibility(View.GONE);
        } else {
            mBinding.fabOption.setVisibility(View.GONE);
            mBinding.btnChat.setVisibility(View.VISIBLE);
        }
    }

    private void setApartment() {
        Glide.with(this).load(mApartment.getApartmentImg()).into(mBinding.apartmentImg);
        mBinding.tvTitle.setText(mApartment.getTitle());
        mBinding.tvAddress.setText(mApartment.getAddress());
        mBinding.tvPrice.setText("₱" + mApartment.getPrice());
        mBinding.tvAvailability.setText(mApartment.getAvailability());
        mBinding.tvDescription.setText(mApartment.getDescription());

        if (mApartment.getLandlordImg().equalsIgnoreCase("null")) {
            mBinding.imgLandlord.setImageResource(R.drawable.img_user_placeholder);
        } else {
            Glide.with(this).load(mApartment.getLandlordImg()).into(mBinding.imgLandlord);
        }

        mBinding.tvLandlordName.setText(mApartment.getLandlordName());
    }

    private void setApartmentAvailabilityIcon() {
        if (mApartment.getAvailability().equalsIgnoreCase("available")) {
            mBinding.iconAvailability.setImageResource(R.drawable.ic_check_circle);
            int color = ContextCompat.getColor(this, android.R.color.holo_green_dark);
            mBinding.iconAvailability.setColorFilter(color);
        } else {
            mBinding.iconAvailability.setImageResource(R.drawable.ic_cancel);
            int color = ContextCompat.getColor(this, android.R.color.holo_red_dark);
            mBinding.iconAvailability.setColorFilter(color);
        }
    }

    private void setDialogOptions() {
        mOptionList = new ArrayList<>();
        mOptionList.add(new ModelOption("Edit Details", R.drawable.ic_edit_document));
        mOptionList.add(new ModelOption("Change Availability", R.drawable.ic_change_circle));
        mOptionList.add(new ModelOption("Delete Apartment", R.drawable.ic_delete));

        mDiaOptions = new DialogOptions(this, "Apartment Options", mOptionList, this);
    }

    private void chatLandlord() {
        ModelUser landlord = new ModelUser();
        landlord.setUid(mApartment.getLandlordUid());
        landlord.setDisplayName(mApartment.getLandlordName());
        landlord.setPhotoUrl(mApartment.getLandlordImg());

        Intent intent = new Intent(this, ActivityConversation.class);
        intent.putExtra("user", landlord);
        startActivity(intent);
    }

    @Override
    public void onDialogOptionClick(int position) {
        mDiaOptions.dismiss();

        ModelOption option = mOptionList.get(position);
        String name = option.getName().toLowerCase();

        switch (name) {
            case "edit details":
                Intent intent = new Intent(this, ActivityEditApartment.class);
                intent.putExtra("apartment", mApartment);
                startActivityForResult(intent, 1);
                break;
            case "change availability":
                setAvailability();
                updateAvailabilityOnline();
                break;
            case "delete apartment":
                showDialogConfirmDelete();
                break;
        }
    }

    private void showDialogConfirmDelete() {
        DialogConfirm diaConfirmDelete = new DialogConfirm(this, this);

        diaConfirmDelete.setHeadline("Delete this apartment?");
        diaConfirmDelete.setMessage("This will permanently delete the apartment.");

        diaConfirmDelete.setNegativeButtonText("Cancel");
        diaConfirmDelete.setPositiveButtonText("Delete");

        diaConfirmDelete.delayedShow();
    }

    private void setAvailability() {
        String availability = mApartment.getAvailability();
        if (availability.equalsIgnoreCase("Available")) {
            mApartment.setAvailability("Not available");
        } else mApartment.setAvailability("Available");
    }

    private void updateAvailabilityOnline() {
        DialogLoading diaLoading = new DialogLoading(this);
        diaLoading.show();

        DbApartments dbApartments = new DbApartments();
        dbApartments.changeAvailability(mApartment)
                .addOnSuccessListener(s -> {
                    diaLoading.dismiss();
                    setApartmentAvailabilityIcon();
                    mBinding.tvAvailability.setText(mApartment.getAvailability());
                    Toast.makeText(this, "Availability changed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    diaLoading.dismiss();
                    Toast.makeText(this, "Availability not changed", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                mApartment = data.getParcelableExtra("apartment");
                setApartment();
            }
        }
    }

    @Override //On Apartment Delete
    public void onPositiveButtonClick() {
        DialogLoading diaLoading = new DialogLoading(this);
        diaLoading.delayedShow();

        StorageApartmentsImg storageApartments = new StorageApartmentsImg(getContentResolver());
        storageApartments.deleteImage(mApartment)
                .addOnSuccessListener(s -> {
                    DbApartments dbApartments = new DbApartments();
                    dbApartments.deleteApartment(mApartment)
                            .addOnSuccessListener(s1 -> {
                                diaLoading.dismiss();
                                showShortToast("Apartment deleted");
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                diaLoading.dismiss();
                                showShortToast("Failed to delete");
                            });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    diaLoading.dismiss();
                    showShortToast("Failed to delete");
                });
    }

    private void showShortToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
