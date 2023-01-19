package com.neoniequell.locapartment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.jakewharton.processphoenix.ProcessPhoenix;
import com.neoniequell.locapartment.databinding.DialogThemeBinding;

public class DialogTheme extends Dialog implements RadioGroup.OnCheckedChangeListener {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NIGHT_MODE = "nightMode";

    private SharedPreferences.Editor mEditor;

    private DialogThemeBinding mBinding;
    private Context mContext;

    public DialogTheme(@NonNull Context context) {
        super(context);
        mContext = context;

        SharedPreferences sharedPrefs = getContext()
                .getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        mEditor = sharedPrefs.edit();

        setProperty();
        getActiveTheme();

        mBinding.radioGroup.setOnCheckedChangeListener(this);
        mBinding.btnClose.setOnClickListener(v -> dismiss());
    }

    private void setProperty() {
        mBinding = DialogThemeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int radius = (int) mContext.getResources().getDimension(R.dimen.shape_extra_large);
        mBinding.rlParent.setBackground(UtilDialog.getBackground(mContext, radius));
    }

    private void getActiveTheme() {
        int nightModeValue = AppCompatDelegate.getDefaultNightMode();

        if (nightModeValue == AppCompatDelegate.MODE_NIGHT_NO) mBinding.rbLight.setChecked(true);
        else if (nightModeValue == AppCompatDelegate.MODE_NIGHT_YES) {
            mBinding.rbDark.setChecked(true);
        } else mBinding.rbSystemDefault.setChecked(true);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        int selectedRb = radioGroup.getCheckedRadioButtonId();

        switch (selectedRb) {
            case R.id.rb_light:
                mEditor.putString(NIGHT_MODE, "MODE_NIGHT_NO");
                break;
            case R.id.rb_dark:
                mEditor.putString(NIGHT_MODE, "MODE_NIGHT_YES");
                break;
            case R.id.rb_system_default:
                mEditor.putString(NIGHT_MODE, "MODE_NIGHT_FOLLOW_SYSTEM");
                break;
        }

        mEditor.apply();
        dismiss();

        ProcessPhoenix.triggerRebirth(mContext.getApplicationContext());
    }
}
