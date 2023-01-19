package com.neoniequell.locapartment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.elevation.SurfaceColors;
import com.neoniequell.locapartment.databinding.ItemMyProfileOptionBinding;

import java.util.List;

public class AdapterMyProfileParent extends
        RecyclerView.Adapter<AdapterMyProfileParent.ViewHolder> {

    private List<ModelMyProfileParent> mMyProfileOptionList;

    private Context mContext;
    private OnOptionClickListener mListener;

    public AdapterMyProfileParent(Context context,
                                  List<ModelMyProfileParent> myProfileOptionList,
                                  OnOptionClickListener listener) {
        mContext = context;
        mMyProfileOptionList = myProfileOptionList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemMyProfileOptionBinding binding = ItemMyProfileOptionBinding
                .inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelMyProfileParent optionParent = mMyProfileOptionList.get(position);

        holder.mBinding.tvGroupTitle.setText(optionParent.getTitle());

        int color = SurfaceColors.SURFACE_2.getColor(mContext);
        holder.mBinding.tvGroupTitle.setBackgroundColor(color);

        //Set recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);

        AdapterMyProfileChild adapter = new AdapterMyProfileChild(optionParent.getOptionList(),
                childPosition -> mListener.onOptionClick(holder.getAdapterPosition(),
                        childPosition));

        DividerItemDecoration divider = new DividerItemDecoration(
                holder.mBinding.recyclerView.getContext(), DividerItemDecoration.VERTICAL);

        holder.mBinding.recyclerView.setLayoutManager(layoutManager);
        holder.mBinding.recyclerView.setAdapter(adapter);
        holder.mBinding.recyclerView.addItemDecoration(divider);
    }

    @Override
    public int getItemCount() {
        return mMyProfileOptionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemMyProfileOptionBinding mBinding;

        public ViewHolder(@NonNull ItemMyProfileOptionBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    public interface OnOptionClickListener {
        void onOptionClick(int parentPosition, int childPosition);
    }
}
