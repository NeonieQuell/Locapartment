package com.neoniequell.locapartment;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.elevation.SurfaceColors;

public class UtilSearchView {

    private UtilSearchView() {

    }

    public static Typeface getTypeface(Context context) {
        return ResourcesCompat.getFont(context, R.font.poppins_regular);
    }

    public static ShapeDrawable getBackground(Context context, float radius) {
        ShapeDrawable shape = new ShapeDrawable(new RoundRectShape(new float[]{radius, radius,
                radius, radius, radius, radius, radius, radius}, null, null));
        shape.getPaint().setColor(SurfaceColors.SURFACE_1.getColor(context));

        return shape;
    }

    //For setting icon, hint, and text color from attributes
    /*public static int getIconHintTextColor(Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        boolean attribute = theme.resolveAttribute(com.google.android.material
                .R.attr.{color}, typedValue, true);
        int colorFromTheme = 0;

        if (attribute) colorFromTheme = typedValue.data;

        return colorFromTheme;
    }*/
}
