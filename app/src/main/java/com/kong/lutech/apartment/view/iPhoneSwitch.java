package com.kong.lutech.apartment.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.kongtech.lutech.apartment.R;

/**
 * Created by gimdonghyeog on 2017. 5. 25..
 */

public class iPhoneSwitch extends CompoundButton {
    private static final String TAG = "iPhoneSwitch";

    private Drawable mTrackDrawable;
    private Drawable mThumbDrawable;

    private int mTrackWidth, mTrackHeight;
    private int mThumbWidth, mThumbHeight;

    public iPhoneSwitch(Context context) {
        super(context);
        init();
    }

    public iPhoneSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public iPhoneSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public iPhoneSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");
        mTrackWidth = mTrackDrawable.getIntrinsicWidth();
        mTrackHeight = mTrackDrawable.getIntrinsicHeight();

        mThumbWidth = mThumbDrawable.getIntrinsicWidth();
        mThumbHeight = mThumbDrawable.getIntrinsicHeight();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init() {
        Log.d(TAG, "init");
        setClickable(true);
        mTrackDrawable = getResources().getDrawable(R.drawable.appleswitch_track_off);

        mThumbDrawable = getResources().getDrawable(R.drawable.appleswitch_thumb);
    }

    @Override
    public void toggle() {
        super.toggle();

        setChecked(!isChecked());
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);

        //mTrackDrawable = trackDrawables[ isChecked() ? 0 : 1 ];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final Drawable trackDrawable = mTrackDrawable;
        trackDrawable.setBounds(0, 0, mTrackWidth, mTrackHeight);

        if (mTrackDrawable != null) mTrackDrawable.draw(canvas);
        Log.d(TAG, "mTrackDrawable is null : " + (mTrackDrawable == null));
        Log.d(TAG, "Canvas Width : "+ canvas.getWidth() + ", Canvas Height : " + canvas.getHeight());
        Log.d(TAG, "Track Width : " + mTrackWidth + ", Track Height : "+ mTrackHeight);
    }
}
