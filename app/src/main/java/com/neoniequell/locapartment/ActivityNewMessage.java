package com.neoniequell.locapartment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.neoniequell.locapartment.databinding.ActivityNewMessageBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityNewMessage extends AppCompatActivity
        implements AdapterUser.OnUserClickListener, AdapterUser.OnUserLongClickListener {

    private DbUsers mDbUsers;

    private List<ModelUser> mUserList;

    private ActivityNewMessageBinding mBinding;
    private InputMethodManager mInputMethodManager;

    private AdapterUser mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityNewMessageBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mDbUsers = new DbUsers();
        mUserList = new ArrayList<>();

        mInputMethodManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        setActionBar();
        setSearchViewProperty();
        setRecyclerView();
        getAllUsers();

        mBinding.shimmerFl.startShimmer();
        mBinding.searchView.setOnQueryTextListener(mFilterRv);
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.appBar.toolbar);
        getSupportActionBar().setTitle("New Message");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        mBinding.appBar.toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
    }

    private void setSearchViewProperty() {
        TextView searchText = mBinding.searchView.findViewById(
                androidx.appcompat.R.id.search_src_text);

        searchText.setTypeface(UtilSearchView.getTypeface(this));
        searchText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.typescale_body_large));
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mAdapter = new AdapterUser(this, mUserList, this, this);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    private void getAllUsers() {
        mDbUsers.getAllUsers(userList -> {
            mUserList.clear();
            mUserList.addAll(userList);

            mAdapter = new AdapterUser(this, userList, this, this);
            mBinding.recyclerView.setAdapter(mAdapter);

            mBinding.shimmerFl.stopShimmer();
            mBinding.shimmerFl.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.searchView.requestFocus();
        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mInputMethodManager.hideSoftInputFromWindow(mBinding.searchView.getWindowToken(), 0);
    }

    private SearchView.OnQueryTextListener mFilterRv = new SearchView.OnQueryTextListener() {
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
    public void onUserClick(ModelUser user) {
        Intent intent = new Intent(this, ActivityConversation.class);
        intent.putExtra("user", user);
        startActivity(intent);

        finish();
    }

    @Override
    public void onUserLongClick(ModelUser user) {

    }
}
