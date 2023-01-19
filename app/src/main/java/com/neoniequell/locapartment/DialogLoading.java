package com.neoniequell.locapartment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.neoniequell.locapartment.databinding.DialogLoadingBinding;

public class DialogLoading extends Dialog {

    private Handler mHandler;
    private Context mContext;

    public DialogLoading(@NonNull Context context) {
        super(context);
        mContext = context;

        mHandler = new Handler(Looper.getMainLooper());

        setProperty();
    }

    private void setProperty() {
        DialogLoadingBinding binding = DialogLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int radius = (int) mContext.getResources().getDimension(R.dimen.shape_extra_large);
        binding.llParent.setBackground(UtilDialog.getBackground(mContext, radius));

        setCancelable(false);
    }

    public void delayedShow() {
        mHandler.postDelayed(super::show, mContext.getResources()
                .getInteger(android.R.integer.config_shortAnimTime));
    }
}
