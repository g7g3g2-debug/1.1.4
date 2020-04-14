package com.kong.lutech.apartment.view;

/**
 * Created by kimdonghyuk on 2017. 3. 21..
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kongtech.lutech.apartment.R;

public class NotificationTab extends FrameLayout {

    private ImageView icon;
    private FrameLayout flNoti;
    private TextView tvNotiCnt;

    public NotificationTab(@NonNull Context context) {
        super(context);
        init();
    }

    public NotificationTab(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NotificationTab(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NotificationTab(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.tab_notification, this);

        icon = (ImageView)findViewById(R.id.icon);
        flNoti = (FrameLayout)findViewById(R.id.flNoti);
        tvNotiCnt = (TextView)findViewById(R.id.tvNotiCnt);
    }

    public void setData(int resources, int cnt) {
        icon.setImageResource(resources);
        if (cnt > 0) {
            this.flNoti.setVisibility(View.VISIBLE);
            this.tvNotiCnt.setText(String.valueOf(cnt));
        } else {
            this.flNoti.setVisibility(View.GONE);
        }
        this.tvNotiCnt.setText(String.valueOf(cnt));
    }

    public void setCount(int cnt) {
        if (cnt > 0) {
            this.flNoti.setVisibility(View.VISIBLE);
            this.tvNotiCnt.setText(String.valueOf(cnt));
        } else {
            this.flNoti.setVisibility(View.GONE);
        }
        this.tvNotiCnt.setText(String.valueOf(cnt));
    }

    public void setSelected(boolean selected){
        icon.setSelected(selected);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        /*if (isNotification) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);

            final int WIDTH = canvas.getWidth();
            final int HEIGHT = canvas.getHeight();

            //final int MARGIN = WIDTH/3 * 2 - WIDTH/2;
            final int MARGIN = 12;
            final Rect rect = new Rect(WIDTH/3 * 2 - MARGIN, MARGIN, WIDTH - MARGIN, WIDTH/3 + MARGIN);

            canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width()/2, paint);
            //canvas.drawRect(rect, paint);
            Paint textPaint = new Paint();
            textPaint.setTextSize(24);
            textPaint.setColor(0xffffffff);
            canvas.drawText("1",rect.centerX() - 12, rect.centerY(), textPaint);
        }*/
    }
}
