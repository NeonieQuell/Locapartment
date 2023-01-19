package com.neoniequell.locapartment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.neoniequell.locapartment.databinding.ItemMessageLeftBinding;
import com.neoniequell.locapartment.databinding.ItemMessageRightBinding;

import java.util.List;
import java.util.Objects;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.ViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private String mPhotoUrl;

    private FirebaseAuth mAuth;

    private List<ModelMessage> mMessageList;
    private Context mContext;

    public AdapterMessage(Context context, List<ModelMessage> messageList, String photoUrl) {
        mContext = context;
        mMessageList = messageList;
        mPhotoUrl = photoUrl;

        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == MSG_TYPE_RIGHT) {
            ItemMessageRightBinding binding = ItemMessageRightBinding.inflate(
                    inflater, parent, false);
            return new ViewHolder(binding);
        } else {
            ItemMessageLeftBinding binding = ItemMessageLeftBinding.inflate(
                    inflater, parent, false);
            return new ViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelMessage message = mMessageList.get(position);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (holder.getItemViewType() == MSG_TYPE_RIGHT) {
            //Check if current message is the last item in arraylist
            if (Objects.equals(message, mMessageList.get(mMessageList.size() - 1))) {
                params.setMargins(
                        UtilPixelToDp.marginInDp(mContext, 120),
                        UtilPixelToDp.marginInDp(mContext, 16),
                        UtilPixelToDp.marginInDp(mContext, 16),
                        UtilPixelToDp.marginInDp(mContext, 16));
            } else {
                params.setMargins(
                        UtilPixelToDp.marginInDp(mContext, 120),
                        UtilPixelToDp.marginInDp(mContext, 16),
                        UtilPixelToDp.marginInDp(mContext, 16),
                        UtilPixelToDp.marginInDp(mContext, 0));
            }

            holder.mRightBinding.rlParent.setLayoutParams(params);
            holder.mRightBinding.tvMessage.setText(message.getMessage());
        } else {
            //Check if current message is the last item in arraylist
            if (Objects.equals(message, mMessageList.get(mMessageList.size() - 1))) {
                params.setMargins(
                        UtilPixelToDp.marginInDp(mContext, 16),
                        UtilPixelToDp.marginInDp(mContext, 16),
                        UtilPixelToDp.marginInDp(mContext, 72),
                        UtilPixelToDp.marginInDp(mContext, 16)
                );
            } else {
                params.setMargins(
                        UtilPixelToDp.marginInDp(mContext, 16),
                        UtilPixelToDp.marginInDp(mContext, 16),
                        UtilPixelToDp.marginInDp(mContext, 72),
                        UtilPixelToDp.marginInDp(mContext, 0));
            }

            holder.mLeftBinding.rlParent.setLayoutParams(params);
            Glide.with(mContext).load(mPhotoUrl).into(holder.mLeftBinding.accountImg);
            holder.mLeftBinding.tvMessage.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessageList.get(position).getSender().equals(mAuth.getUid())) return MSG_TYPE_RIGHT;
        else return MSG_TYPE_LEFT;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemMessageRightBinding mRightBinding;
        private ItemMessageLeftBinding mLeftBinding;

        public ViewHolder(@NonNull ItemMessageRightBinding binding) {
            super(binding.getRoot());
            mRightBinding = binding;
        }

        public ViewHolder(@NonNull ItemMessageLeftBinding binding) {
            super(binding.getRoot());
            mLeftBinding = binding;
        }
    }
}
