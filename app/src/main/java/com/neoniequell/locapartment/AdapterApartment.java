package com.neoniequell.locapartment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.neoniequell.locapartment.databinding.ItemApartmentBinding;

import java.util.List;

public class AdapterApartment extends RecyclerView.Adapter<AdapterApartment.ViewHolder> {

    private List<ModelApartment> mApartmentList;

    private Context mContext;
    private OnApartmentClickListener mListener;

    public AdapterApartment(Context context, List<ModelApartment> apartmentList,
                            OnApartmentClickListener listener) {
        mContext = context;
        mApartmentList = apartmentList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemApartmentBinding binding = ItemApartmentBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelApartment apartment = mApartmentList.get(position);

        if (apartment.getLandlordImg().equalsIgnoreCase("null")) {
            holder.mBinding.landlordImg.setImageResource(R.drawable.img_user_placeholder);
        } else {
            Glide.with(mContext).load(apartment.getLandlordImg()).into(holder.mBinding.landlordImg);
        }

        Glide.with(mContext).load(apartment.getApartmentImg()).into(holder.mBinding.apartmentImg);
        holder.mBinding.tvLandlordName.setText(apartment.getLandlordName());
        holder.mBinding.tvApartmentPrice.setText("₱" + apartment.getPrice());
        holder.mBinding.tvApartmentTitle.setText(apartment.getTitle());
        holder.mBinding.tvApartmentAddress.setText(apartment.getAddress());
    }

    @Override
    public int getItemCount() {
        return mApartmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemApartmentBinding mBinding;
        private OnApartmentClickListener mListener;

        public ViewHolder(@NonNull ItemApartmentBinding binding,
                          OnApartmentClickListener clickListener) {
            super(binding.getRoot());
            mBinding = binding;
            mListener = clickListener;

            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onApartmentClick(getAdapterPosition());
        }
    }

    public interface OnApartmentClickListener {
        void onApartmentClick(int position);
    }
}
