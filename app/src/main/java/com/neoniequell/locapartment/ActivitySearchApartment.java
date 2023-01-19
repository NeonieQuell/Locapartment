package com.neoniequell.locapartment;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.neoniequell.locapartment.databinding.ActivitySearchApartmentBinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ActivitySearchApartment extends AppCompatActivity implements
        AdapterApartmentSimple.OnApartmentClickListener,
        DialogOptions.OnDialogOptionClickListener {

    private List<ModelOption> mSortPricesOptionList;
    private List<ModelApartment> mApartmentList;

    private ActivitySearchApartmentBinding mBinding;

    private AdapterApartmentSimple mAdapter;

    private DialogOptions mDiaOptions;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySearchApartmentBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mApartmentList = new ArrayList<>();

        mBinding.shimmerFl.startShimmer();

        setActionBar();
        setSearchViewProperty();
        setRecyclerView();
        getApartments();

        mBinding.searchView.setOnQueryTextListener(filterRv);
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        mBinding.toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
    }

    private void setSearchViewProperty() {
        TextView searchText = mBinding.searchView.findViewById(
                androidx.appcompat.R.id.search_src_text);

        searchText.setTypeface(UtilSearchView.getTypeface(this));
        searchText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.typescale_body_large));

        int radius = getResources().getInteger(R.integer.search_view_radius);
        float radiusInFloat = UtilPixelToDp.marginInDp(this, radius);
        mBinding.searchView.setBackground(UtilSearchView.getBackground(this, radiusInFloat));
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
        dbApartments.getApartments(apartmentList -> {
            mApartmentList.clear();
            mApartmentList.addAll(apartmentList);

            mAdapter = new AdapterApartmentSimple(mApartmentList, this);
            mBinding.recyclerView.setAdapter(mAdapter);

            mBinding.shimmerFl.stopShimmer();
            mBinding.shimmerFl.setVisibility(View.GONE);

            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search_apartment_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title = String.valueOf(item.getTitle());
        if (title.equalsIgnoreCase("sort prices")) showSortPricesDialog();
        return super.onOptionsItemSelected(item);
    }

    private void showSortPricesDialog() {
        mSortPricesOptionList = new ArrayList<>();

        mSortPricesOptionList.add(new ModelOption("Lowest to Highest",
                R.drawable.ic_trending_up));

        mSortPricesOptionList.add(new ModelOption("Highest to Lowest",
                R.drawable.ic_trending_down));

        mSortPricesOptionList.add(new ModelOption("Default",
                R.drawable.ic_trending_flat));

        mDiaOptions = new DialogOptions(this, "Sort Prices", mSortPricesOptionList, this);
        mDiaOptions.show();
    }

    private SearchView.OnQueryTextListener filterRv = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mAdapter.getFilter().filter(newText);
            return false;
        }
    };

    @Override
    public void onApartmentClick(ModelApartment apartment) {
        Intent intent = new Intent(this, ActivityViewApartment.class);
        intent.putExtra("apartment", apartment);
        startActivity(intent);
    }

    @Override
    public void onDialogOptionClick(int position) {
        mDiaOptions.dismiss();

        ModelOption option = mSortPricesOptionList.get(position);
        String optionName = option.getName();

        if (optionName.equalsIgnoreCase("lowest to highest")) {
            List<ModelApartment> lowToHigh = new ArrayList<>(mApartmentList);
            lowToHigh.sort(Comparator.comparingDouble(ModelApartment::getPrice));
            mAdapter = new AdapterApartmentSimple(lowToHigh, this);
        } else if (optionName.equalsIgnoreCase("highest to lowest")) {
            List<ModelApartment> highToLowList = new ArrayList<>(mApartmentList);
            highToLowList.sort((o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice()));
            mAdapter = new AdapterApartmentSimple(highToLowList, this);
        } else mAdapter = new AdapterApartmentSimple(mApartmentList, this);

        mBinding.recyclerView.setAdapter(mAdapter);
        showShortToast(optionName);
    }

    private void showShortToast(String message) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
