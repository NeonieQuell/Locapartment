package com.neoniequell.locapartment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.navigation.NavigationBarView;
import com.neoniequell.locapartment.databinding.ActivityMainBinding;

public class ActivityMain extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setViewPager();

        mBinding.btmNavView.setOnItemSelectedListener(navigateMainFragments);
    }

    private void setViewPager() {
        AdapterViewPager adapter = new AdapterViewPager(getSupportFragmentManager(), getLifecycle());
        adapter.add(new FragmentHome());
        adapter.add(new FragmentChats());
        adapter.add(new FragmentMyProfile());

        mBinding.viewPager.setAdapter(adapter);
        mBinding.viewPager.setUserInputEnabled(false);
    }

    private NavigationBarView.OnItemSelectedListener navigateMainFragments = item -> {
        switch (item.getItemId()) {
            case R.id.menu_btm_nav_view_home:
                mBinding.viewPager.setCurrentItem(0, false);
                mBinding.btmNavView.getMenu()
                        .findItem(R.id.menu_btm_nav_view_home).setChecked(true);
                break;
            case R.id.menu_btm_nav_view_chats:
                mBinding.viewPager.setCurrentItem(1, false);
                mBinding.btmNavView.getMenu()
                        .findItem(R.id.menu_btm_nav_view_chats).setChecked(true);
                break;
            case R.id.menu_btm_nav_view_my_profile:
                mBinding.viewPager.setCurrentItem(2, false);
                mBinding.btmNavView.getMenu()
                        .findItem(R.id.menu_btm_nav_view_my_profile).setChecked(true);
                break;
        }
        return false;
    };
}
