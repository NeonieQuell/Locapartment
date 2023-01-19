package com.neoniequell.locapartment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.neoniequell.locapartment.databinding.FragmentChatsBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentChats extends Fragment implements
        AdapterUser.OnUserClickListener, AdapterUser.OnUserLongClickListener {

    private List<ModelUser> mUserList;

    private FragmentChatsBinding mBinding;
    private Context mContext;
    private AppCompatActivity mActivity;

    private AdapterUser mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentChatsBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        mActivity = (AppCompatActivity) getActivity();
        mActivity.setSupportActionBar(mBinding.appBar.toolbar);
        mActivity.getSupportActionBar().setTitle(mContext.getString(R.string.chats));

        mUserList = new ArrayList<>();

        setSearchViewProperty();
        setRecyclerView();
        getUserWithConvo();

        mBinding.shimmerFl.startShimmer();
        mBinding.searchView.setOnQueryTextListener(filterRv);
        mBinding.swipeLayout.setOnRefreshListener(this::getUserWithConvo);

        return mBinding.getRoot();
    }

    private void setSearchViewProperty() {
        TextView searchText = mBinding.searchView.findViewById(
                androidx.appcompat.R.id.search_src_text);

        searchText.setTypeface(UtilSearchView.getTypeface(mContext));
        searchText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.typescale_body_large));

        int radius = mContext.getResources().getInteger(R.integer.search_view_radius);
        float radiusInFloat = UtilPixelToDp.marginInDp(mContext, radius);
        mBinding.searchView.setBackground(UtilSearchView.getBackground(mContext, radiusInFloat));
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);

        mAdapter = new AdapterUser(mContext, mUserList, this, this);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    private void getUserWithConvo() {
        DbConversations dbConvo = new DbConversations();
        dbConvo.getUserWithConvo(userWithConvoList -> {
            if (userWithConvoList.isEmpty()) mBinding.llIndicator.setVisibility(View.VISIBLE);
            else {
                dbConvo.getUserWithConversationInfo(userWithConvoList, userList -> {
                    mUserList.clear();
                    mUserList.addAll(userList);

                    mBinding.llIndicator.setVisibility(View.GONE);

                    mAdapter = new AdapterUser(mContext, userList, this, this);
                    mBinding.recyclerView.setAdapter(mAdapter);
                });
            }

            mBinding.shimmerFl.stopShimmer();
            mBinding.shimmerFl.setVisibility(View.GONE);

            if (mBinding.swipeLayout.isRefreshing()) mBinding.swipeLayout.setRefreshing(false);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuInflater menuInflater = mActivity.getMenuInflater();
        menuInflater.inflate(R.menu.menu_frag_chats_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title = String.valueOf(item.getTitle());
        if (title.equalsIgnoreCase("new message")) {
            startActivity(new Intent(mActivity, ActivityNewMessage.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.searchView.clearFocus();
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
    public void onUserClick(ModelUser user) {
        Intent intent = new Intent(mActivity, ActivityConversation.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public void onUserLongClick(ModelUser user) {

    }
}
