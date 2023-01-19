package com.neoniequell.locapartment;

import android.content.Context;
import android.util.TypedValue;

public class UtilPixelToDp {

    private UtilPixelToDp() {

    }

    public static int marginInDp(Context context, int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, context.getResources().getDisplayMetrics());
    }

    public static float marginInDp(Context context, float value) {
        return (float) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, context.getResources().getDisplayMetrics());
    }
}
