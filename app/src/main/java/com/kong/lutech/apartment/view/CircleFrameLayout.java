package com.kong.lutech.apartment.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * Created by kimdonghyuk on 2017. 3. 22..
 */

public class CircleFrameLayout extends FrameLayout {

    private int color = 0xffff0000;

    public CircleFrameLayout(@NonNull Context context) {
        super(context);
    }

    public CircleFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        Paint redPaint = new Paint();
        redPaint.setColor(color);
        //Log.d("CircleFrameLayout", "Width : "+canvas.getWidth()+", Height : "+canvas.getHeight());
        final float WIDTH = canvas.getWidth();
        final float HEIGHT = canvas.getHeight();
        canvas.drawCircle(WIDTH/2, HEIGHT/2, WIDTH/2, redPaint);

        super.dispatchDraw(canvas);
    }
}
