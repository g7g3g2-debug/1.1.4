package com.kong.lutech.apartment.utils;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.kongtech.lutech.apartment.R;

public class DrawableUtil {
    public static void SetBackgroundDrawable(View v, int backgroundColor, int borderColor) {
        Context context = v.getContext();
        float radius = context.getResources().getDimension(R.dimen.button_radius);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
        shape.setColor(backgroundColor);
        shape.setStroke((int) context.getResources().getDimension(R.dimen.button_stroke), borderColor);
        v.setBackground(shape);
    }

    public static void SetBackgroundDrawable(View v, int backgroundColor, int borderColor, float dimension) {
        Context context = v.getContext();
        float radius = dimension;
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
        shape.setColor(backgroundColor);
        shape.setStroke((int) context.getResources().getDimension(R.dimen.button_stroke), borderColor);
        v.setBackground(shape);
    }
}
