package com.neoniequell.locapartment;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

import com.google.android.material.elevation.SurfaceColors;

public class UtilDialog {
    
    private UtilDialog() {
        
    }

    public static ShapeDrawable getBackground(Context context, float radius) {
        ShapeDrawable shape = new ShapeDrawable(new RoundRectShape(new float[]{radius, radius,
                radius, radius, radius, radius, radius, radius}, null, null));
        shape.getPaint().setColor(SurfaceColors.SURFACE_3.getColor(context));

        return shape;
    }
}
