package com.neoniequell.locapartment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neoniequell.locapartment.databinding.ItemApartmentSimpleBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterApartmentSimple extends
        RecyclerView.Adapter<AdapterApartmentSimple.ViewHolder> implements Filterable {

    private List<ModelApartment> mApartmentList;
    private List<ModelApartment> mApartmentListFull;

    private OnApartmentClickListener mListener;

    public AdapterApartmentSimple(List<ModelApartment> apartmentList,
                                  OnApartmentClickListener listener) {
        mApartmentList = apartmentList;
        mListener = listener;

        mApartmentListFull = new ArrayList<>(apartmentList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        ItemApartmentSimpleBinding binding;
        binding = ItemApartmentSimpleBinding.inflate(inflater, parent, false);

        return new ViewHolder(binding, mApartmentList, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelApartment apartment = mApartmentList.get(position);

        holder.mBinding.tvApartmentTitle.setText(apartment.getTitle());
        holder.mBinding.tvApartmentAddress.setText(apartment.getAddress());
        holder.mBinding.tvApartmentPrice.setText("₱" + apartment.getPrice());
    }

    @Override
    public int getItemCount() {
        return mApartmentList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ModelApartment> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mApartmentListFull);
            } else {
                String filterPattern = String.valueOf(constraint).toLowerCase().trim();

                for (ModelApartment apartment : mApartmentListFull) {
                    if (apartment.getAddress().toLowerCase().contains(filterPattern)) {
                        filteredList.add(apartment);
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
            mApartmentList.clear();
            mApartmentList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private List<ModelApartment> mApartmentList;

        private ItemApartmentSimpleBinding mBinding;
        private OnApartmentClickListener mListener;

        public ViewHolder(@NonNull ItemApartmentSimpleBinding binding,
                          List<ModelApartment> apartmentList,
                          OnApartmentClickListener listener) {
            super(binding.getRoot());
            mBinding = binding;
            mApartmentList = apartmentList;
            mListener = listener;

            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onApartmentClick(mApartmentList.get(getAdapterPosition()));
        }
    }

    public interface OnApartmentClickListener {
        void onApartmentClick(ModelApartment apartment);
    }
}
