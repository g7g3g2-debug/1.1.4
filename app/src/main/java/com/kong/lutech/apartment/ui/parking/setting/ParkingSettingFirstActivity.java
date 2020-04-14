package com.kong.lutech.apartment.ui.parking.setting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.kong.lutech.apartment.Config;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.adapter.HintSpinnerAdapter;
import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.model.ParkImage;
import com.kong.lutech.apartment.network.api.parking.ParkingRestUtil;
import com.kong.lutech.apartment.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ParkingSettingFirstActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();

    public static final String EXTRA_PARK_IMAGE = "PARKIMAGE";

    private static final int REQUEST_CODE_SETTING = 2001;


    private List<ParkImage> parkImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_setting_first);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        homeButtonEnabled();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Log.d(TAG, "dongSequence : " + Config.getMobile().getDongSequence());
        Log.d(TAG, "homeSequence : " + Config.getMobile().getHomeSequence());

        final ArrayAdapter parkingLotAdapter = new HintSpinnerAdapter<>(getApplicationContext(), R.layout.spinner_item, "주차장을 선택해 주세요.", new ArrayList<ParkImage>());
        parkingLotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final AppCompatSpinner spinnerParkingLot = findViewById(R.id.spinnerParkingLot);
        spinnerParkingLot.setAdapter(parkingLotAdapter);
        spinnerParkingLot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                findViewById(R.id.btnNext).setEnabled( position > 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "정보 가져오는중...", true, false);

        final String accessToken = Config.getAccessToken();
        final Mobile mobile = Config.getMobile();

        ParkingRestUtil.getIntsance(getApplicationContext()).getParkImages(accessToken, mobile.getApartmentId())
                .compose(bindToLifecycle())
                .filter( parkImageListResponse -> {
                    if (parkImageListResponse.isSuccessful() && parkImageListResponse.body() != null) {
                        return true;
                    } else {
                        throw new Error("주차장 정보를 불러오는데 실패하였습니다.");
                    }
                })
                .map( parkImageListResponse -> parkImageListResponse.body().getData())
                .subscribe( parkImages -> {
                            progressDialog.dismiss();

                            this.parkImages = parkImages;

                            parkingLotAdapter.addAll(parkImages);
                            parkingLotAdapter.notifyDataSetChanged();
                        },
                        throwable -> {
                            progressDialog.dismiss();

                            new AlertDialog.Builder(this).setMessage(throwable.getMessage()).setPositiveButton("확인", (dialog, which) -> {
                                dialog.dismiss();
                            }).show();

                            throwable.printStackTrace();
                            Log.e(TAG, "" + throwable.getMessage());
                        }
                );


        findViewById(R.id.btnNext).setOnClickListener(v -> {
            int position = spinnerParkingLot.getSelectedItemPosition();

            if (position > 0) position -= 1;
            if (position >= parkImages.size()) return;

            final Intent intent = new Intent(getApplicationContext(), ParkingSettingSecondActivity.class);
            intent.putExtra(ParkingSettingFirstActivity.EXTRA_PARK_IMAGE, parkImages.get(position));

            startActivityForResult(intent, REQUEST_CODE_SETTING);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ParkingSettingFirstActivity.REQUEST_CODE_SETTING && resultCode == RESULT_OK) {
            setResult(resultCode);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
