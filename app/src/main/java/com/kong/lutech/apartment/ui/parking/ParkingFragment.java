package com.kong.lutech.apartment.ui.parking;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kong.lutech.apartment.ui.parking.detail.ParkingDetailActivity;
import com.kong.lutech.apartment.ui.parking.setting.ParkingSettingFirstActivity;
import com.kong.lutech.apartment.Config;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.adapter.viewpager.ParkingCarPagerAdapter;
import com.kong.lutech.apartment.model.Car;
import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.model.ParkImage;
import com.kong.lutech.apartment.model.ParkImageParkingInfo;
import com.kong.lutech.apartment.model.PreferedParkingInfo;
import com.kong.lutech.apartment.network.api.apartment.ApartmentRestUtil;
import com.kong.lutech.apartment.network.api.parking.ParkingRestUtil;
import com.kong.lutech.apartment.ui.MainActivity;

import com.kong.lutech.apartment.utils.Optional;
import com.kong.lutech.apartment.view.CircleIndicator;
import com.kong.lutech.apartment.view.ZoomableImageView;
import com.kongtech.smapsdk.ble.BLEManager;
import com.kongtech.smapsdk.services.ParkingManager;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.List;


public class ParkingFragment extends RxFragment implements ParkingCarPagerAdapter.OnParkingCarClickListener, View.OnClickListener {
    private String TAG = getClass().getSimpleName();

    private static final int REQUEST_CODE_SETTING_PARKING = 2020;

    private boolean isViewShown = false;


    private TextView tvParkingLot;

    private ZoomableImageView ivParkingLot;
    private LinearLayout llParkingLotEmpty;

    private CircleIndicator clIndicator;
    private ParkingCarPagerAdapter parkingCarAdapter;

    private ParkingManager parkingManager;

    private BLEManager bleManager;


    private BroadcastReceiver newParkingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String ACTION = intent.getAction();
            if (ACTION.equals(MainActivity.INTENT_NEW_PARKING)) {
                loadParkingCars();
                requestPreferedParkingInfo();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("주차위치 저장");
        final View view = inflater.inflate(R.layout.fragment_parking, container, false);

        // 상단

        tvParkingLot = view.findViewById(R.id.tvParkingLot);
        ivParkingLot = view.findViewById(R.id.ivParkingLot);
        ivParkingLot.setTouchEnabled(false);

        llParkingLotEmpty = view.findViewById(R.id.llParkingLotEmpty);
        view.findViewById(R.id.btnParkingLotSetting).setOnClickListener(this);

        // 하단

        final ViewPager vpParkingCar = view.findViewById(R.id.vpParkingCar);
        vpParkingCar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                clIndicator.setIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        clIndicator = view.findViewById(R.id.clIndicator);

        parkingCarAdapter = new ParkingCarPagerAdapter(getFragmentManager());
        parkingCarAdapter.setOnParkingCarClickListener(this);

        vpParkingCar.setAdapter(parkingCarAdapter);

        bleManager = new BLEManager();
        if (!bleManager.isEnabled()) {
            new AlertDialog.Builder(getActivity())
                    .setMessage("앱에서 제공하는 블루투스 기능을 사용하기 위해서는 블루투스를 활성화 해 주세요.")
                    .setPositiveButton("활성화", ((dialog, which) -> bleManager.enable()))
                    .setNegativeButton("취소", null)
                    .show();
        }
        parkingManager = new ParkingManager(getActivity());

        final Bundle arguments = getArguments();
        if (arguments != null && getArguments().getBoolean("FIRST", false) && !isViewShown) {
            displayParkingDisplay();
        }

        Log.d("SectionsPagerAdapter", "ParkingFragment called");

        return view;
    }

    @Override
    public void onCarDetailClick(Car car) {
        final Intent detailIntent = new Intent(getActivity(), ParkingDetailActivity.class);
        detailIntent.putExtra("Car", car);
        detailIntent.putExtra("UsePreference", true);
        startActivity(detailIntent);
    }

    private ProgressDialog parkingDialog;

    @Override
    public void onCarParkClick(Car car) {
        if (parkingManager != null) {
            parkingDialog = ProgressDialog.show(getActivity(), "주차장 위치 파악 중입니다.", "잠시만 기다려 주세요.", true, false);

            parkingManager.startParking(car.getCode(), new ParkingManager.OnParkingCallback() {
                @Override
                public void onParkingSuccess(String carCode) {
                    if (parkingDialog != null && parkingDialog.isShowing()) parkingDialog.dismiss();
                }

                @Override
                public void onParkingFailure(String carCode, int errorCode) {
                    if (parkingDialog != null && parkingDialog.isShowing()) parkingDialog.dismiss();

                    if (errorCode == 1) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage("앱에서 제공하는 블루투스 기능을 사용하기 위해서는 블루투스를 활성화 해 주세요.")
                                .setPositiveButton("활성화", ((dialog, which) -> bleManager.enable()))
                                .setNegativeButton("취소", null)
                                .show();
                    } else if (errorCode == 2) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("주차실패")
                                .setMessage("주차하기 기능을 사용할 수 없는 기기 입니다.")
                                .setPositiveButton("확인", null)
                                .show();
                    } else if (errorCode == 3 || errorCode == 5) {
                        showNoneParkingSnackbar();
                    }
                }
            });
        }
    }

    private void showNoneParkingSnackbar() {
        Snackbar snackbar = Snackbar.make(getView(), "", Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(0xffe85757);

        layout.findViewById(android.support.design.R.id.snackbar_text).setVisibility(View.INVISIBLE);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.snack_dont_parking, layout, false);
        layout.addView(view, 0);

        snackbar.show();
    }

    @Override
    public void onDestroy() {
        if (isRegisteredReceiver) {
            getActivity().unregisterReceiver(newParkingReceiver);
            isRegisteredReceiver = false;
        }
        super.onDestroy();
    }

    private void loadParkingCars() {
        if (Config.Check()) {
            final Mobile mobile = Config.getMobile();
            ApartmentRestUtil.getInstance(getActivity(), "yyyy-MM-dd hh:mm:ss")
                    .getCars(Config.getAccessToken(), mobile.getApartmentId(), mobile.getDongId(), mobile.getHomeId())
                    .compose(bindToLifecycle())
                    .subscribe(carListResponse -> {
                        if (carListResponse.isSuccessful()) {
                            Log.d(TAG, "Request ParkingCar Success");
                            if (carListResponse.body() != null) {
                                final List<Car> cars = carListResponse.body().getCars();
                                this.parkingCarAdapter.setParkingCars(cars);

                                this.clIndicator.setCount(cars.size());
                            }
                        } else {
                            Log.d(TAG, "Request ParkingCar Error");
                        }
                    }, Throwable::printStackTrace);
        }
    }

    private void requestPreferedParkingInfo() {
        Log.d(TAG, "requestPreferedParkingInfo");
        ParkingRestUtil.getIntsance(getActivity()).getPreferedParkingInfo(Config.getAccessToken())
                .compose(bindToLifecycle())
                .filter( preferedParkingInfoResponse -> {
                    if ( preferedParkingInfoResponse.isSuccessful() && preferedParkingInfoResponse.body() != null ) {
                        return true;
                    } else {
                        throw new Error("선호 주차 구역을 불러오지 못했습니다.");
                    }
                } )
                .flatMap(preferedParkingInfoResponse -> {
                    final PreferedParkingInfo preferedParkingInfo = preferedParkingInfoResponse.body();

                    return ParkingRestUtil.getIntsance(getActivity()).getParkImageParkingInfo(Config.getAccessToken(), Config.getMobile().getApartmentId(), preferedParkingInfo.getParkImage().getSequence())
                            .filter( parkImageParkingInfoResponse -> {

                                if ( parkImageParkingInfoResponse.isSuccessful() && parkImageParkingInfoResponse.body() != null && parkImageParkingInfoResponse.body().getData() != null ) {
                                    return true;
                                } else {
                                    final ParkImage parkImage = preferedParkingInfo.getParkImage();
                                    Log.d(TAG, "주차구역 : " + parkImage.getParkName());

                                    getActivity().runOnUiThread(() -> {
                                        this.tvParkingLot.setText("주차구역 : " + parkImage.getParkName());
                                        this.llParkingLotEmpty.setVisibility(View.INVISIBLE);

                                        this.ivParkingLot.setVisibility(View.VISIBLE);
                                        this.ivParkingLot.setFocus(ivParkingLot.getWidth(), ivParkingLot.getHeight());

                                        new Thread(() -> Glide.with(getActivity()).asBitmap().load(parkImage.getPhotoPath()).listener(new RequestListener<Bitmap>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                                //final Bitmap bitmap = ZoomableImageView.getBitmapFromURL(parkImage.getPhotoPath(), null);
                                                if (resource == null) return false;

                                                getActivity().runOnUiThread(() -> {
                                                    ivParkingLot.setImageBitmap(resource);
                                                    ivParkingLot.setMapSize(parkImage.getMapWidth(), parkImage.getMapHeight());

                                                    final String locations = preferedParkingInfo.getPreferedParkLocation();

                                                    final String[] splited = locations.split("\\^");
                                                    if (splited.length == 4) {
                                                        ivParkingLot.setInsets(new float[]{Float.parseFloat(splited[0]), Float.parseFloat(splited[1]), Float.parseFloat(splited[2]), Float.parseFloat(splited[3])});
                                                    }
                                                });

                                                return false;
                                            }
                                        })).start();
                                    });
                                    throw new Error("주차 이미지 정보를 불러오지 못했습니다.");
                                }
                            })
                            .map(parkImageParkingInfoDataResponse -> new Optional<>(parkImageParkingInfoDataResponse.body().getData()))
                            .onErrorReturnItem(new Optional<>(null))
                            .map( parkImageParkingInfo -> {
                                final ParkDataResponse parkDataResponse = new ParkDataResponse();
                                parkDataResponse.preferedParkingInfo = preferedParkingInfo;
                                parkDataResponse.parkImageParkingInfo = parkImageParkingInfo.isEmpty() ? null : parkImageParkingInfo.get();

                                return parkDataResponse;
                            });
                })
                .subscribe(parkResponse -> {
                    final PreferedParkingInfo preferedParkingInfo = parkResponse.preferedParkingInfo;
                    final ParkImageParkingInfo parkImageParkingInfo = parkResponse.parkImageParkingInfo;

                    final ParkImage parkImage = preferedParkingInfo.getParkImage();
                    this.tvParkingLot.setText("주차구역 : " + parkImage.getParkName());
                    this.llParkingLotEmpty.setVisibility(View.INVISIBLE);

                    this.ivParkingLot.setVisibility(View.VISIBLE);
                    this.ivParkingLot.setFocus(ivParkingLot.getWidth() / 2, ivParkingLot.getHeight() / 2);

                    new Thread(() -> {

                        Bitmap bitmap = ZoomableImageView.getBitmapFromURL(parkImage.getPhotoPath(), null);
                        if (bitmap == null) return;

                        if (parkImageParkingInfo != null && parkImageParkingInfo.getLocations() != null) {
                            final List<ParkImageParkingInfo.Location> locations1 = parkImageParkingInfo.getLocations();
                            bitmap = ZoomableImageView.mergeParkingLocationBitmap(bitmap, locations1);
                        }

                        Bitmap finalBitmap = bitmap;
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                ivParkingLot.setImageBitmap(finalBitmap);
                                ivParkingLot.setMapSize(parkImage.getMapWidth(), parkImage.getMapHeight());

                                final String locations = preferedParkingInfo.getPreferedParkLocation();

                                final String[] splited = locations.split("\\^");
                                if (splited.length == 4) {
                                    ivParkingLot.setInsets(new float[]{Float.parseFloat(splited[0]), Float.parseFloat(splited[1]), Float.parseFloat(splited[2]), Float.parseFloat(splited[3])});
                                }
                            });
                        }
                    }).start();

                }, throwable -> {
                    this.ivParkingLot.setVisibility(View.INVISIBLE);
                    this.llParkingLotEmpty.setVisibility(View.VISIBLE);
                    this.tvParkingLot.setText("선호 주차구역이 없습니다");
                });
    }

    private boolean isRegisteredReceiver = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint : " + isVisibleToUser);

        if (getView() != null) {
            isViewShown = true;
            displayParkingDisplay();
        } else {
            isViewShown = false;
        }

        if (!isVisibleToUser && isRegisteredReceiver) {
            getActivity().unregisterReceiver(newParkingReceiver);
            isRegisteredReceiver = false;
        }
    }

    private void displayParkingDisplay() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.INTENT_NEW_PARKING);
        if (getActivity() != null) getActivity().registerReceiver(newParkingReceiver, intentFilter);
        isRegisteredReceiver = true;

        loadParkingCars();
        requestPreferedParkingInfo();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnParkingLotSetting) {
            final Intent settingIntent = new Intent(getActivity(), ParkingSettingFirstActivity.class);
            startActivityForResult(settingIntent, ParkingFragment.REQUEST_CODE_SETTING_PARKING);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            requestPreferedParkingInfo();
        }
    }


    public static class ParkDataResponse {
        public PreferedParkingInfo preferedParkingInfo;
        public ParkImageParkingInfo parkImageParkingInfo;
    }
}
