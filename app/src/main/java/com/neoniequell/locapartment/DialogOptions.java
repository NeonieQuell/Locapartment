package com.neoniequell.locapartment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.neoniequell.locapartment.databinding.DialogOptionsBinding;

import java.util.List;

public class DialogOptions extends Dialog implements AdapterDialogOptions.OnOptionClickListener {

    private List<ModelOption> mOptionList;

    private DialogOptionsBinding mBinding;
    private Context mContext;
    private OnDialogOptionClickListener mListener;

    public DialogOptions(@NonNull Context context, String headline, List<ModelOption> optionList,
                         OnDialogOptionClickListener listener) {
        super(context);
        mContext = context;
        mOptionList = optionList;
        mListener = listener;

        setProperty();
        setRecyclerView();

        mBinding.tvHeadline.setText(headline);
    }

    private void setProperty() {
        mBinding = DialogOptionsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int radius = (int) mContext.getResources().getDimension(R.dimen.shape_extra_large);
        mBinding.rlParent.setBackground(UtilDialog.getBackground(mContext, radius));
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        AdapterDialogOptions adapter = new AdapterDialogOptions(mContext, mOptionList, this);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onOptionClick(int position) {
        mListener.onDialogOptionClick(position);
    }

    public interface OnDialogOptionClickListener {
        void onDialogOptionClick(int position);
    }
}
