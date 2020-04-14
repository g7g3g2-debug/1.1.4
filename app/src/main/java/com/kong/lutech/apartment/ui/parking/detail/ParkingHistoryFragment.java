package com.kong.lutech.apartment.ui.parking.detail;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kong.lutech.apartment.Config;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.adapter.recyclerview.ParkingHistoryRecyclerAdapter;
import com.kong.lutech.apartment.model.Car;
import com.kong.lutech.apartment.model.CctvLog;
import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.model.list.CctvLogList;
import com.kong.lutech.apartment.network.api.apartment.ApartmentRestUtil;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParkingHistoryFragment extends RxFragment
        implements Updateable, SwipyRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = getClass().getSimpleName();

    private SwipeRefreshLayout swipeRefresh;

    private RecyclerView rvParkingHistory;
    private LinearLayout llNoneParking;

    private SwipyRefreshLayout srlRefresh;
    private ParkingHistoryRecyclerAdapter adapter;

    private static int PAGE_OF_SIZE = 15;
    private int page = 1;
    private int TOTAL_PAGE_COUNT = 1;

    private Car car;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_parking_history, container, false);

        swipeRefresh = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        llNoneParking = (LinearLayout)view.findViewById(R.id.llNoneParking);
        srlRefresh = (SwipyRefreshLayout)view.findViewById(R.id.srlRefresh);
        srlRefresh.setOnRefreshListener(this);
        rvParkingHistory = (RecyclerView)view.findViewById(R.id.rvParkingHistory);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        rvParkingHistory.setLayoutManager(layoutManager);

        adapter = new ParkingHistoryRecyclerAdapter(getActivity());
        rvParkingHistory.setAdapter(adapter);

        car = getArguments().getParcelable("Car");

        loadCctvLogs(1);

        return view;
    }

    private void loadCctvLogs(int page) {
        if (page > TOTAL_PAGE_COUNT) {
            if (srlRefresh.isRefreshing()) srlRefresh.setRefreshing(false);
            return;
        }

        if (Config.Check()) {
            Log.d(TAG, "CarNumber : " + car.getNumber());
            final Mobile mobile = Config.getMobile();
            ApartmentRestUtil.getIntsance(getActivity())
                    .getCctvLogs(Config.getAccessToken(), mobile.getApartmentId(), mobile.getDongId(), mobile.getHomeId(), car.getNumber(), page, PAGE_OF_SIZE)
                    .compose(bindToLifecycle())
                    .subscribe(cctvLogListResponse -> {
                        if (cctvLogListResponse.isSuccessful()) {
                            Log.d(TAG, "Request CctvLogs Success");
                            final CctvLogList cctvLogList = cctvLogListResponse.body();

                            this.page = page + 1;
                            final CctvLogList.PageInfo PAGEINFO = cctvLogList.getPageInfo();
                            TOTAL_PAGE_COUNT = PAGEINFO.getTotalPageCount();

                            if (PAGEINFO.getTotalItemCount() <= 0) {
                                llNoneParking.setVisibility(View.VISIBLE);
                                srlRefresh.setVisibility(View.GONE);
                            } else {
                                llNoneParking.setVisibility(View.GONE);
                                srlRefresh.setVisibility(View.VISIBLE);
                            }
                            adapter.addItems(cctvLogList.getCctvLogs());

                            if (srlRefresh.isRefreshing()) srlRefresh.setRefreshing(false);
                            if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);
                        } else {
                            Log.d(TAG, "Request CctvLogs Error");
                            if (srlRefresh.isRefreshing()) srlRefresh.setRefreshing(false);
                            if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);
                        }
                    }, throwable -> {
                        throwable.printStackTrace();

                        if (srlRefresh.isRefreshing()) srlRefresh.setRefreshing(false);
                        if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);
                    });
        }
    }

    @Override
    public void registerForContextMenu(View view) {
        super.registerForContextMenu(view);
    }

    @Override
    public void update(Bundle bundle) {
        final ArrayList<CctvLog> cctvLogs = bundle.getParcelableArrayList("CctvLogs");

        if (cctvLogs != null && cctvLogs.size() > 0) {
            llNoneParking.setVisibility(View.GONE);
            adapter.setItems(cctvLogs);
        } else {
            llNoneParking.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        loadCctvLogs(page);
    }

    @Override
    public void onRefresh() {
        page = 1;
        TOTAL_PAGE_COUNT = 1;

        adapter.clear();
        loadCctvLogs(page);
    }
}
