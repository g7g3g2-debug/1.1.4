package com.kong.lutech.apartment.ui.main;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kong.lutech.apartment.ui.parking.detail.ParkingDetailActivity;
import com.kong.lutech.apartment.Config;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.adapter.recyclerview.ParkingRecyclerAdapter;
import com.kong.lutech.apartment.model.Car;
import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.network.api.apartment.ApartmentRestUtil;
import com.kong.lutech.apartment.ui.MainActivity;
import com.kongtech.smapsdk.ble.BLEManager;
import com.kongtech.smapsdk.services.ParkingManager;
import com.trello.rxlifecycle2.components.support.RxFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends RxFragment implements ParkingRecyclerAdapter.OnParkingCarClickListener, SwipeRefreshLayout.OnRefreshListener {
    private String TAG = getClass().getSimpleName();

    private boolean isViewShown = false;

    private LinearLayout llNoneCar;

    private SwipeRefreshLayout srlRefresh;
    private RecyclerView listParking;
    private ParkingRecyclerAdapter adapter;

    private ParkingManager parkingManager;

    private BLEManager bleManager;


    private BroadcastReceiver newParkingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String ACTION = intent.getAction();
            if (ACTION.equals(MainActivity.INTENT_NEW_PARKING)) {
                loadParkingCars();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("주차위치 저장");
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        srlRefresh = view.findViewById(R.id.srlRefresh);
        srlRefresh.setOnRefreshListener(this);
        llNoneCar = view.findViewById(R.id.llNoneCar);
        listParking = view.findViewById(R.id.listParking);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        listParking.setLayoutManager(layoutManager);

        adapter = new ParkingRecyclerAdapter(getActivity(), this);
        listParking.setAdapter(adapter);

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

        return view;
    }

    @Override
    public void onCarDetailClick(Car car) {
        final Intent detailIntent = new Intent(getActivity(), ParkingDetailActivity.class);
        detailIntent.putExtra("Car", car);
        detailIntent.putExtra("UsePreference", false);
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
        Snackbar snackbar = Snackbar.make(listParking, "", Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(0xffe85757);

        layout.findViewById(android.support.design.R.id.snackbar_text).setVisibility(View.INVISIBLE);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.snack_dont_parking, layout, false);
        layout.addView(view, 0);

        snackbar.show();
    }

    private void checkItemCount() {
        if (srlRefresh.isRefreshing()) srlRefresh.setRefreshing(false);

        if (adapter.getItemCount() < 1) {
            llNoneCar.setVisibility(View.VISIBLE);
            listParking.setVisibility(View.GONE);
        } else {
            llNoneCar.setVisibility(View.GONE);
            listParking.setVisibility(View.VISIBLE);
        }
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
                            adapter.setItems(carListResponse.body().getCars());

                            checkItemCount();
                        } else {
                            Log.d(TAG, "Request ParkingCar Error");

                            checkItemCount();
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                        checkItemCount();
                    });
        }
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
        getActivity().registerReceiver(newParkingReceiver, intentFilter);
        isRegisteredReceiver = true;

        loadParkingCars();
    }

    @Override
    public void onRefresh() {
        loadParkingCars();
    }
}