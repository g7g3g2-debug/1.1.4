package com.kong.lutech.apartment.ui.parking.setting;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kong.lutech.apartment.Config;
import com.kong.lutech.apartment.model.ParkImage;
import com.kong.lutech.apartment.network.api.parking.ParkingRestUtil;
import com.kong.lutech.apartment.ui.BaseActivity;
import com.kong.lutech.apartment.utils.SizeUtils;
import com.kong.lutech.apartment.view.FocusCardView;
import com.kong.lutech.apartment.view.ZoomableImageView;
import com.kongtech.lutech.apartment.R;

public class ParkingSettingSecondActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();


    private ZoomableImageView ivZoomable;

    private ParkImage parkImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_setting_second);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        homeButtonEnabled();

        parkImage = getIntent().getParcelableExtra(ParkingSettingFirstActivity.EXTRA_PARK_IMAGE);
        if (parkImage == null) {
            new AlertDialog.Builder(this).setMessage("주차장 이미지를 불러오지 못했습니다.").setPositiveButton("확인", (dialog, which) -> finish()).show();
            return;
        }

        findViewById(R.id.btnSave).setOnClickListener(this);
        final FocusCardView focusCardView = findViewById(R.id.cvFocus);

        ivZoomable = findViewById(R.id.ivZoomable);
        ivZoomable.setImageDrawable(getResources().getDrawable(R.drawable.parking));
        ivZoomable.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Glide.with(ParkingSettingSecondActivity.this).load(parkImage.getPhotoPath()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        final int focusWidth = ivZoomable.getWidth() - (int) SizeUtils.convertDpToPixel(32, getApplicationContext());
                        final int focusHeight = ivZoomable.getHeight() - (int) SizeUtils.convertDpToPixel(64, getApplicationContext());

                        ivZoomable.setMapSize(parkImage.getMapWidth(), parkImage.getMapHeight());
                        ivZoomable.setFocus(focusWidth, focusHeight);
                        focusCardView.setFocus(focusWidth, focusHeight, SizeUtils.convertDpToPixel(8, getApplicationContext()));

                        return false;
                    }
                }).into(ivZoomable);

                ivZoomable.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSave) {

            if ( parkImage == null ) return;

            ParkingRestUtil.getIntsance(getApplicationContext()).putPreferedParkingInfo(Config.getAccessToken(), parkImage.getId(), ivZoomable.getInsets())
                    .compose(bindToLifecycle())
                    .filter( preferedParkingInfoResponse -> {
                        if ( preferedParkingInfoResponse.isSuccessful() && preferedParkingInfoResponse.body() != null ) {
                            return true;
                        } else {
                            throw new Error("정보를 설정하는데 실패하였습니다.");
                        }
                    } )
                    .subscribe(preferedParkingInfoResponse -> {
                        new AlertDialog.Builder(this).setMessage("설정이 완료되었습니다.").setPositiveButton("확인", (dialog, which) -> {
                            setResult(RESULT_OK);
                            finish();
                        }).show();

                    }, throwable -> {
                        new AlertDialog.Builder(this).setMessage(throwable.getMessage()).setPositiveButton("확인", (dialog, which) -> {
                            dialog.dismiss();
                        }).show();

                        throwable.printStackTrace();
                    });
        }
    }
}
