package com.neoniequell.locapartment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.neoniequell.locapartment.databinding.FragmentMyProfileBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentMyProfile extends Fragment implements
        AdapterMyProfileParent.OnOptionClickListener {

    private List<ModelMyProfileParent> mMyProfileOptionList;
    private List<ModelMyProfileChild> mActivitiesOptionList;
    private List<ModelMyProfileChild> mAccManagementOptionList;

    private FragmentMyProfileBinding mBinding;
    private Context mContext;
    private AppCompatActivity mActivity;

    private AdapterMyProfileParent mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMyProfileBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        mActivity = (AppCompatActivity) getActivity();
        mActivity.setSupportActionBar(mBinding.appBar.toolbar);
        mActivity.getSupportActionBar().setTitle(mContext.getString(R.string.my_profile));

        mMyProfileOptionList = new ArrayList<>();

        setMyProfileDetails();
        setActivitiesOptionList();
        setAccManagementOptionList();
        setRecyclerView();

        return mBinding.getRoot();
    }

    private void setMyProfileDetails() {
        mBinding.shimmerFl.startShimmer();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        DbUsers dbUsers = new DbUsers();

        dbUsers.getSingleUserInfo(auth.getUid(), user -> {
            if (user.getPhotoUrl().equalsIgnoreCase("null")) {
                mBinding.accountImg.setImageResource(R.drawable.img_user_placeholder);
            } else {
                Glide.with(mContext).load(user.getPhotoUrl()).into(mBinding.accountImg);
            }

            mBinding.tvName.setText(user.getDisplayName());
            mBinding.tvEmail.setText(user.getEmail());

            mBinding.shimmerFl.stopShimmer();
            mBinding.shimmerFl.setVisibility(View.GONE);
        });
    }

    private void setActivitiesOptionList() {
        //Create child options
        mActivitiesOptionList = new ArrayList<>();

        ModelMyProfileChild postAnApartment;
        postAnApartment = new ModelMyProfileChild(R.drawable.ic_post_add, "Post an Apartment");

        ModelMyProfileChild myApartments;
        myApartments = new ModelMyProfileChild(R.drawable.ic_apartment, "My Apartments");

        mActivitiesOptionList.add(postAnApartment);
        mActivitiesOptionList.add(myApartments);

        //Add child options to main
        mMyProfileOptionList.add(new ModelMyProfileParent("Activities", mActivitiesOptionList));
    }

    private void setAccManagementOptionList() {
        //Create child options
        mAccManagementOptionList = new ArrayList<>();

        ModelMyProfileChild theme = new ModelMyProfileChild(R.drawable.ic_brightness_4, "Theme");
        ModelMyProfileChild logOut = new ModelMyProfileChild(R.drawable.ic_logout, "Log Out");

        mAccManagementOptionList.add(theme);
        mAccManagementOptionList.add(logOut);

        //Add child options to main
        mMyProfileOptionList.add(new ModelMyProfileParent("Account Management",
                mAccManagementOptionList));
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);

        mAdapter = new AdapterMyProfileParent(mContext, mMyProfileOptionList, this);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onOptionClick(int parentPosition, int childPosition) {
        switch (parentPosition) {
            case 0:
                String activitiesOption = mActivitiesOptionList
                        .get(childPosition).getName().toLowerCase();

                switch (activitiesOption) {
                    case "post an apartment":
                        startActivity(new Intent(mActivity, ActivityPostApartment.class));
                        break;
                    case "my apartments":
                        startActivity(new Intent(mActivity, ActivityMyApartments.class));
                        break;
                }

                break;
            case 1:
                String accManagementOption = mAccManagementOptionList
                        .get(childPosition).getName().toLowerCase();

                switch (accManagementOption) {
                    case "theme":
                        DialogTheme diaTheme = new DialogTheme(mContext);
                        diaTheme.show();
                        break;
                    case "log out":
                        logOut();
                        break;
                }

                break;
        }
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient gsc = GoogleSignIn.getClient(mContext, gso);
        gsc.signOut();

        mContext.startActivity(new Intent(getActivity(), ActivityLauncher.class));
        getActivity().finish();
    }
}
