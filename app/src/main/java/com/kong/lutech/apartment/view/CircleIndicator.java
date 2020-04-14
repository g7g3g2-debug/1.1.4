package com.kong.lutech.apartment.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.kong.lutech.apartment.utils.SizeUtils;

/**
 * Created by gimdonghyeog on 12/11/2018.
 * KDH
 */
public class CircleIndicator extends FrameLayout {

    private int selectionColor = 0xff40cbe0;
    private int defaultColor = 0xffc8c8c8;

    private int count;
    private int index = 0;

    public CircleIndicator(@NonNull Context context) {
        super(context);
    }

    public CircleIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleIndicator(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleIndicator(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCount(int count) {
        this.count = count;

        invalidate();
    }

    public void setIndex(int index) {
        this.index = index;

        invalidate();
    }

    public int getCount() {
        return count;
    }

    public int getIndex() {
        return index;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        if (count <= 0) { return; }

        Paint selectedPaint = new Paint();
        selectedPaint.setColor(selectionColor);

        Paint defaultPaint = new Paint();
        defaultPaint.setColor(defaultColor);


        final float frameWidth = (count - 1) * SizeUtils.convertDpToPixel(14, getContext());

        for (int i = 0 ; i < count ; i ++) {
            final float posX = frameWidth / (float) (count - 1) * (float) i;

            canvas.drawCircle(canvas.getWidth() / 2 + (Float.isNaN(posX) ? 0 : posX), canvas.getHeight() / 2, SizeUtils.convertDpToPixel(3, getContext()), i == index ? selectedPaint : defaultPaint);
        }

        super.dispatchDraw(canvas);
    }

}
