package com.neoniequell.locapartment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.neoniequell.locapartment.databinding.ItemUserBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolder>
        implements Filterable {

    private List<ModelUser> mUserList;
    private List<ModelUser> mUserListFull;

    private Context mContext;
    private OnUserClickListener mClickListener;
    private OnUserLongClickListener mLongClickListener;

    public AdapterUser(Context context, List<ModelUser> userList,
                       OnUserClickListener clickListener,
                       OnUserLongClickListener longClickListener) {
        mContext = context;
        mUserList = userList;
        mClickListener = clickListener;
        mLongClickListener = longClickListener;

        mUserListFull = new ArrayList<>(userList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemUserBinding binding = ItemUserBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding, mUserList, mClickListener, mLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelUser user = mUserList.get(position);

        if (user.getPhotoUrl().equalsIgnoreCase("null")) {
            holder.mBinding.accountImg.setImageResource(R.drawable.img_user_placeholder);
        } else {
            Glide.with(mContext).load(user.getPhotoUrl()).into(holder.mBinding.accountImg);
        }

        holder.mBinding.tvDisplayName.setText(user.getDisplayName());
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ModelUser> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mUserListFull);
            } else {
                String filterPattern = String.valueOf(constraint).toLowerCase().trim();

                for (ModelUser user : mUserListFull) {
                    if (user.getDisplayName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(user);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mUserList.clear();
            mUserList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        private List<ModelUser> mUserList;

        private ItemUserBinding mBinding;
        private OnUserClickListener mClickListener;
        private OnUserLongClickListener mLongCLickListener;

        public ViewHolder(@NonNull ItemUserBinding binding, List<ModelUser> userList,
                          OnUserClickListener clickListener,
                          OnUserLongClickListener longClickListener) {
            super(binding.getRoot());
            mBinding = binding;
            mUserList = userList;
            mClickListener = clickListener;
            mLongCLickListener = longClickListener;

            binding.getRoot().setOnClickListener(this);
            binding.getRoot().setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onUserClick(mUserList.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View v) {
            mLongCLickListener.onUserLongClick(mUserList.get(getAdapterPosition()));
            return true;
        }
    }

    public interface OnUserClickListener {
        void onUserClick(ModelUser user);
    }

    public interface OnUserLongClickListener {
        void onUserLongClick(ModelUser user);
    }
}
