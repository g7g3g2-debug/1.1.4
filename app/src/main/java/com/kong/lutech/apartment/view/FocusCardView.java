package com.kong.lutech.apartment.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.transition.CircularPropagation;
import android.util.AttributeSet;
import android.view.View;

public class FocusCardView extends View {

    private Paint mFocusPaint;
    private Paint mBackPaint;

    private int focusWidth;
    private int focusHeight;
    private float focusRadius;

    public FocusCardView(Context context) {
        this(context, null);
    }

    public FocusCardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FocusCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackPaint.setColor(0x7b111e29);

        mFocusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFocusPaint.setColor(0x00ffffff);
        mFocusPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setFocus(int width, int height, float radius) {
        this.focusWidth = width;
        this.focusHeight = height;
        this.focusRadius = radius;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        canvas.drawRect(0, 0, w, h, mBackPaint);
        canvas.drawRoundRect((w - focusWidth) / 2
                , (h - focusHeight) / 2
                , (w + focusWidth) / 2
                , (h + focusHeight) / 2
                , focusRadius, focusRadius, mFocusPaint);
    }
}
