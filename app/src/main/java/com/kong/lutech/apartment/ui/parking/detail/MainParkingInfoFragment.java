package com.kong.lutech.apartment.ui.parking.detail;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kong.lutech.apartment.Config;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.model.Car;
import com.kong.lutech.apartment.model.CctvLog;
import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.network.api.apartment.ApartmentInterface;
import com.kong.lutech.apartment.network.api.apartment.ApartmentRestUtil;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainParkingInfoFragment extends RxFragment {
    private final String TAG = getClass().getSimpleName();

    private ImageView ivThumbnail;
    private TextView tvCarNumber, tvLocation, tvDate;

    private Car car;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_parking_info, container, false);

        ivThumbnail = view.findViewById(R.id.ivThumbnail);
        tvCarNumber = view.findViewById(R.id.tvCarNumber);
        tvLocation = view.findViewById(R.id.tvCarLocation);
        tvDate = view.findViewById(R.id.tvDate);

        car = getArguments().getParcelable("Car");
        tvCarNumber.setText(car.getNumber());

        loadCctvLog();

        return view;
    }

    private void loadCctvLog() {
        if (Config.Check()) {
            final Mobile mobile = Config.getMobile();
            ApartmentRestUtil.getIntsance(getActivity())
                    .getLatestCctvLog(Config.getAccessToken(), mobile.getApartmentId(), car.getCarId())
                    .compose(bindToLifecycle())
                    .subscribe(cctvLogResponse -> {
                        if (cctvLogResponse.isSuccessful()) {
                            Log.d(TAG, "Request CctvLogs Success");

                            final ApartmentInterface.LatestCctvLog latestCctvLog = cctvLogResponse.body();
                            if (latestCctvLog == null) return;

                            final CctvLog cctvLog = latestCctvLog.getCctvLog();
                            if (cctvLog == null) return;

                            if ( cctvLog.getPicture() != null ) {
                                byte[] decodedString = Base64.decode(cctvLog.getPicture(), Base64.DEFAULT);
                                final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                ivThumbnail.setImageBitmap(decodedByte);
                            }
                            if (!TextUtils.isEmpty(cctvLog.getCarNumber())) tvCarNumber.setText(cctvLog.getCarNumber());

                            final StringBuilder locationStr = new StringBuilder();
                            if (!TextUtils.isEmpty(cctvLog.getParkName())) {
                                locationStr.append(cctvLog.getParkName());
                            }
                            if (!TextUtils.isEmpty(cctvLog.getParkName()) && !TextUtils.isEmpty(cctvLog.getCctvName())) {
                                locationStr.append(", ");
                                locationStr.append(cctvLog.getCctvName());
                            }
                            if (!TextUtils.isEmpty(locationStr)) tvLocation.setText(locationStr.toString());

                            if (cctvLog.getDate() != null) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd  a hh:mm", Locale.ENGLISH);
                                tvDate.setVisibility(View.VISIBLE);
                                tvDate.setText(dateFormat.format(cctvLog.getDate()));
                            } else {
                                tvDate.setVisibility(View.GONE);
                            }
                        } else {
                            failLoadParkingInfo();
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                        failLoadParkingInfo();
                    });
        }
    }

    private void failLoadParkingInfo() {
        new AlertDialog.Builder(getActivity()).setMessage("주차 정보를 읽어오는데 실패하였습니다.").setPositiveButton("확인", null).show();
    }
}
