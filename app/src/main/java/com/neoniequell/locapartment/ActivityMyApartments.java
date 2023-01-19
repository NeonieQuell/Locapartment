package com.neoniequell.locapartment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.elevation.SurfaceColors;
import com.neoniequell.locapartment.databinding.ActivityMyApartmentsBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityMyApartments extends AppCompatActivity implements
        AdapterApartmentSimple.OnApartmentClickListener {

    private List<ModelApartment> mApartmentList;

    private ActivityMyApartmentsBinding mBinding;

    private AdapterApartmentSimple mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMyApartmentsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mApartmentList = new ArrayList<>();

        mBinding.shimmerFl.startShimmer();

        setActionBar();
        setRecyclerView();
        getApartments();

        mBinding.swipeLayout.setOnRefreshListener(() -> {
            mBinding.swipeLayout.setRefreshing(true);
            getApartments();
        });
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.appBar.toolbar);
        getSupportActionBar().setTitle("My Apartments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        mBinding.appBar.toolbar.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(this));

        mBinding.appBar.toolbar.setNavigationOnClickListener(v -> super.onBackPressed());

        //Set status bar color
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new AdapterApartmentSimple(mApartmentList, this);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    private void getApartments() {
        DbApartments dbApartments = new DbApartments();
        dbApartments.getUserApartments(apartmentList -> {
            mApartmentList.clear();

            if (apartmentList.isEmpty()) {
                mBinding.llIndicator.setVisibility(View.VISIBLE);
            } else {
                mApartmentList.addAll(apartmentList);
                mBinding.llIndicator.setVisibility(View.GONE);
            }

            mBinding.shimmerFl.stopShimmer();
            mBinding.shimmerFl.setVisibility(View.GONE);

            mAdapter.notifyDataSetChanged();

            if (mBinding.swipeLayout.isRefreshing()) mBinding.swipeLayout.setRefreshing(false);
        });
    }

    @Override
    public void onApartmentClick(ModelApartment apartment) {
        Intent intent = new Intent(this, ActivityViewApartment.class);
        intent.putExtra("apartment", apartment);
        startActivity(intent);
    }
}
