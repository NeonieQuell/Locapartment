package com.neoniequell.locapartment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.neoniequell.locapartment.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment implements
        AdapterApartment.OnApartmentClickListener {

    private List<ModelApartment> mApartmentList;

    private FragmentHomeBinding mBinding;
    private Context mContext;
    private AppCompatActivity mActivity;

    private AdapterApartment mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        mActivity = (AppCompatActivity) getActivity();
        mActivity.setSupportActionBar(mBinding.appBar.toolbar);
        mActivity.getSupportActionBar().setTitle(mContext.getString(R.string.home));

        mBinding.shimmerFl.startShimmer();

        getApartments();
        setRecyclerView();

        mBinding.swipeLayout.setOnRefreshListener(() -> {
            mBinding.swipeLayout.setRefreshing(true);
            getApartments();
        });

        return mBinding.getRoot();
    }

    private void getApartments() {
        DbApartments dbApartments = new DbApartments();
        dbApartments.getApartments(apartmentList -> {
            mApartmentList.clear();

            if (apartmentList.isEmpty()) {
                mBinding.llIndicator.setVisibility(View.VISIBLE);
            } else {
                mBinding.llIndicator.setVisibility(View.GONE);
                mApartmentList.addAll(apartmentList);
            }

            mBinding.shimmerFl.stopShimmer();
            mBinding.shimmerFl.setVisibility(View.GONE);

            mAdapter.notifyDataSetChanged();

            if (mBinding.swipeLayout.isRefreshing()) mBinding.swipeLayout.setRefreshing(false);
        });
    }

    private void setRecyclerView() {
        mApartmentList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mAdapter = new AdapterApartment(mContext, mApartmentList, this);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onApartmentClick(int position) {
        Intent intent = new Intent(mContext, ActivityViewApartment.class);
        intent.putExtra("apartment", mApartmentList.get(position));
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuInflater menuInflater = mActivity.getMenuInflater();
        menuInflater.inflate(R.menu.menu_frag_home_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title = String.valueOf(item.getTitle());

        if (title.equals(getString(R.string.search))) {
            startActivity(new Intent(mActivity, ActivitySearchApartment.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
