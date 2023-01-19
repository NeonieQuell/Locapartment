package com.neoniequell.locapartment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neoniequell.locapartment.databinding.ItemOptionBinding;

import java.util.List;

public class AdapterOptions extends RecyclerView.Adapter<AdapterOptions.ViewHolder> {

    private List<ModelOption> mOptionList;

    private Context mContext;
    private OnOptionClickListener mListener;

    public AdapterOptions(Context context, List<ModelOption> optionList,
                          OnOptionClickListener listener) {
        mContext = context;
        mOptionList = optionList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemOptionBinding binding = ItemOptionBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelOption option = mOptionList.get(position);

        holder.mBinding.icon.setImageResource(option.getIcon());
        holder.mBinding.tvOptionName.setText(option.getName());
    }

    @Override
    public int getItemCount() {
        return mOptionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ItemOptionBinding mBinding;
        private OnOptionClickListener mListener;

        public ViewHolder(@NonNull ItemOptionBinding binding, OnOptionClickListener listener) {
            super(binding.getRoot());
            mBinding = binding;
            mListener = listener;

            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onOptionClick(getAdapterPosition());
        }
    }

    public interface OnOptionClickListener {
        void onOptionClick(int position);
    }
}
