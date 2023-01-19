package com.neoniequell.locapartment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;

import com.neoniequell.locapartment.databinding.DialogAlertBinding;

public class DialogAlert extends Dialog implements View.OnClickListener {

    private Handler mHandler;

    private DialogAlertBinding mBinding;
    private Context mContext;
    private OnDialogAlertDismissListener mListener;

    public DialogAlert(@NonNull Context context) {
        super(context);
        mContext = context;

        init();
    }

    public DialogAlert(@NonNull Context context, OnDialogAlertDismissListener listener) {
        super(context);
        mContext = context;
        mListener = listener;

        init();
    }

    private void init() {
        mHandler = new Handler(Looper.getMainLooper());

        setProperty();

        mBinding.btnOk.setOnClickListener(this);
    }

    private void setProperty() {
        mBinding = DialogAlertBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int radius = (int) mContext.getResources().getDimension(R.dimen.shape_extra_large);
        mBinding.rlParent.setBackground(UtilDialog.getBackground(mContext, radius));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            dismiss();
            if (mListener != null) mListener.onDialogAlertDismiss();
        }
    }

    public void delayedShow() {
        mHandler.postDelayed(super::show, mContext.getResources()
                .getInteger(android.R.integer.config_shortAnimTime));
    }

    public void setContent(String headline, String message) {
        mBinding.tvHeadline.setText(headline);
        mBinding.tvMessage.setText(message);
    }

    public void setHeadline(String headline) {
        mBinding.tvHeadline.setText(headline);
    }

    public void setMessage(String message) {
        mBinding.tvMessage.setText(message);
    }

    public interface OnDialogAlertDismissListener {
        void onDialogAlertDismiss();
    }
}
