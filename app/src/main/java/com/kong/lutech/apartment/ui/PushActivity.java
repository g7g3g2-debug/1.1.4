package com.kong.lutech.apartment.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kong.lutech.apartment.Config;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.model.Permission;
import com.kong.lutech.apartment.utils.sqlite.DBManager;
import com.kong.lutech.apartment.view.CircleFrameLayout;

public class PushActivity extends Activity implements View.OnClickListener {
    public static final String EXTRA_INDEX_KEY = "INDEX";

    private FrameLayout flParkingContainer, flDeliveryContainer, flNoticeContainer;
    private ImageView ivPark, ivDelivery, ivNotice;
    private CircleFrameLayout cflDelivery, cflNotice;
    private TextView tvDeliveryCnt, tvNoticeCnt;
    private TextView tvClose;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        flParkingContainer = findViewById(R.id.flParkingContainer);
        flDeliveryContainer = findViewById(R.id.flDeliveryContainer);
        flNoticeContainer = findViewById(R.id.flNoticeContainer);

        final Permission permission = Config.getPermission();
        flParkingContainer.setVisibility(permission.isParkingInfo() ? View.VISIBLE : View.GONE);
        flDeliveryContainer.setVisibility(permission.isDelivery() ? View.VISIBLE : View.GONE);
        flNoticeContainer.setVisibility(permission.isNotice() ? View.VISIBLE : View.GONE);


        ivPark = (ImageView)findViewById(R.id.ivPark);
        ivDelivery = (ImageView)findViewById(R.id.ivDelivery);
        ivNotice = (ImageView)findViewById(R.id.ivNotice);
        ivPark.setOnClickListener(this);
        ivDelivery.setOnClickListener(this);
        ivNotice.setOnClickListener(this);

        cflDelivery = (CircleFrameLayout)findViewById(R.id.cflDelivery);
        cflNotice = (CircleFrameLayout)findViewById(R.id.cflNotice);

        tvDeliveryCnt = (TextView)findViewById(R.id.tvDeliveryCnt);
        tvNoticeCnt = (TextView)findViewById(R.id.tvNoticeCnt);

        tvClose = (TextView)findViewById(R.id.tvClose);
        tvClose.setOnClickListener((view) -> finish());

        loadNotiCount();

        powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "SMAP Background Popup");
        wakeLock.acquire();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (wakeLock != null) wakeLock.release();
    }

    private void loadNotiCount() {
        final DBManager dbManager = new DBManager(getApplicationContext()).open();
        final int noticeCount = dbManager.noticesCount();
        final int deliveryCount = dbManager.deliveriesCount();
        dbManager.close();

        if (noticeCount > 0) cflNotice.setVisibility(View.VISIBLE);
        else cflNotice.setVisibility(View.GONE);
        tvNoticeCnt.setText(String.valueOf(noticeCount));

        if (deliveryCount > 0) cflDelivery.setVisibility(View.VISIBLE);
        else cflDelivery.setVisibility(View.GONE);
        tvDeliveryCnt.setText(String.valueOf(deliveryCount));
    }

    @Override
    public void onClick(View view) {
        if (Config.isLogin(getApplicationContext())) {
            Config.settingData(getApplicationContext());
        }

        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        switch (view.getId()) {
            case R.id.ivPark:
                intent.putExtra(EXTRA_INDEX_KEY, 0);
                break;
            case R.id.ivDelivery:
                intent.putExtra(EXTRA_INDEX_KEY, 2);
                break;
            case R.id.ivNotice:
                intent.putExtra(EXTRA_INDEX_KEY, 3);
                break;
        }
        startActivity(intent);
        finishAffinity();
    }
}
