package com.kong.lutech.apartment.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kongtech.lutech.apartment.R;

/**
 * Created by kimdonghyuk on 2017. 3. 21..
 */

public class RecyclerItemView extends LinearLayout {

    private ImageView icon;
    private TextView tvContent, tvSub, tvDate;

    private Drawable onIcon, offIcon;
    private String content, sub, date;
    private boolean isEnabled = true;

    public RecyclerItemView(Context context) {
        super(context);
        init();
    }

    public RecyclerItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(context, attrs);
    }

    public RecyclerItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttrs(context, attrs, defStyleAttr);
    }

    public RecyclerItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        initAttrs(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        inflate(getContext(), R.layout.item_recycler, this);

        icon = (ImageView)findViewById(R.id.item_recycler_icon);
        tvContent = (TextView)findViewById(R.id.item_recycler_tvContent);
        tvSub = (TextView)findViewById(R.id.item_recycler_tvSub);
        tvDate = (TextView)findViewById(R.id.item_recycler_tvDate);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecyclerItemView);
        setAttr(typedArray);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecyclerItemView, defStyleAttr, 0);
        setAttr(typedArray);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecyclerItemView, defStyleAttr, defStyleRes);
        setAttr(typedArray);

    }

    public void setValue(String content, String sub, String date) {
        this.content = content;
        this.sub = sub;
        this.date = date;

        reloadWidgets();
    }

    public void setValue(String content, String sub, String date, boolean isEnabled) {
        this.content = content;
        this.sub = sub;
        this.date = date;
        this.isEnabled = isEnabled;

        reloadWidgets();
    }

    private void setAttr(TypedArray typedArray) {
        content = typedArray.getString(R.styleable.RecyclerItemView_content);
        sub = typedArray.getString(R.styleable.RecyclerItemView_sub);
        date = typedArray.getString(R.styleable.RecyclerItemView_date);
        onIcon = typedArray.getDrawable(R.styleable.RecyclerItemView_onIcon);
        offIcon = typedArray.getDrawable(R.styleable.RecyclerItemView_offIcon);
        isEnabled = typedArray.getBoolean(R.styleable.RecyclerItemView_enabled, true);

        reloadWidgets();

        typedArray.recycle();
    }

    private void reloadWidgets() {
        tvContent.setText(content);
        tvDate.setText(date);
        if (sub == null){
            tvSub.setVisibility(View.GONE);
        } else {
            tvSub.setVisibility(View.VISIBLE);
            tvSub.setText(sub);
        }

        if (isEnabled){
            icon.setImageDrawable(onIcon);
            tvContent.setTextColor(0xff384d5a);
            tvDate.setTextColor(0xff787878);
        } else {
            icon.setImageDrawable(offIcon);
            tvContent.setTextColor(0xffc8c8c8);
            tvDate.setTextColor(0xffc8c8c8);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;

        reloadWidgets();
    }
}
