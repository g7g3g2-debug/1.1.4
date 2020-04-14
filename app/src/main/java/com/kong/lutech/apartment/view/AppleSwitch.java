package com.kong.lutech.apartment.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.kongtech.lutech.apartment.R;

/**
 * Created by kimdonghyuk on 2017. 3. 22..
 */

public class AppleSwitch extends FrameLayout
        implements View.OnClickListener{
    private static final String TAG = "AppleSwitch";

    private int WIDTH = 0, HEIGHT = 0;
    private int MARGIN = 0;

    private int THUMB_WIDTH = 0, THUMB_HEIGHT = 0;


    private int checkTrackColor = 0xff90a3aa;
    private int uncheckTrackColor = 0xffe3e3e3;

    private int thumbColor = 0xffffffff;

    private int thumbmargin = 4;

    private int radius = 0;

    private boolean isChecked = false;

    private View thumb;

    private OnClickListener onClickListener;

    public AppleSwitch(Context context) {
        super(context);
        init(context);
    }

    public AppleSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppleSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AppleSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        onClickListener = l;
    }

    private void init(Context context) {
        super.setOnClickListener(this);
        inflate(context, R.layout.apple_switch, this);

        thumb = findViewById(R.id.appleswitch_thumb);
        ViewCompat.setElevation(thumb, 4);

        WIDTH = (int) getResources().getDimension(R.dimen.appleswitch_width);
        HEIGHT = (int) getResources().getDimension(R.dimen.appleswitch_height);
        MARGIN = (int) getResources().getDimension(R.dimen.appleswitch_margin);
        THUMB_WIDTH = (int) getResources().getDimension(R.dimen.appleswitch_thumb_width);
        THUMB_HEIGHT = (int) getResources().getDimension(R.dimen.appleswitch_thumb_height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        radius = (HEIGHT - thumbmargin * 2) / 2;

        final Paint trackPaint = new Paint();
        trackPaint.setColor(isChecked ? checkTrackColor : uncheckTrackColor);

        //Left Rounded
        RectF arcRect = new RectF(0,0,HEIGHT,HEIGHT);
        canvas.drawArc(arcRect, 90, 180, true, trackPaint);
        //Right Rounded
        arcRect.set(WIDTH-HEIGHT,0,WIDTH,HEIGHT);
        canvas.drawArc(arcRect, 270, 180, true, trackPaint);

        Rect rectRect = new Rect(HEIGHT/2, 0, WIDTH-HEIGHT/2, HEIGHT);
        canvas.drawRect(rectRect, trackPaint);

        super.dispatchDraw(canvas);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        invalidate();
        moveThumb();
    }

    @Override
    public void onClick(View view) {
        setChecked(!isChecked);
        moveThumb();
        if (onClickListener != null) {
            onClickListener.onClick(view);
        }
    }

    private void moveThumb() {
        final float Y = thumb.getPivotY() - thumb.getHeight()/2;
        final TranslateAnimation anim;
        if (isChecked) {
            anim = new TranslateAnimation(MARGIN, WIDTH - THUMB_WIDTH - MARGIN, Y, Y);
        } else {
            anim = new TranslateAnimation(WIDTH - THUMB_WIDTH - MARGIN, MARGIN, Y, Y);
        }

        anim.setDuration(150);
        anim.setFillAfter(true);
        thumb.startAnimation(anim);
    }
}
