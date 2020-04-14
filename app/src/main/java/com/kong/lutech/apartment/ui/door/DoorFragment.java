package com.kong.lutech.apartment.ui.door;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kong.lutech.apartment.Config;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.adapter.recyclerview.GateHistoryRecyclerAdapter;
import com.kong.lutech.apartment.model.GateLog;
import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.model.list.GateLogList;
import com.kong.lutech.apartment.network.api.apartment.ApartmentRestUtil;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.trello.rxlifecycle2.components.support.RxFragment;

public class DoorFragment extends RxFragment implements GateHistoryRecyclerAdapter.OnGateLogClickListener, SwipyRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "DoorFragment";

    private boolean isViewShown = false;

    private int page = 1;
    private int TOTAL_PAGE_COUNT = 1;

    private LinearLayout llNoneDoor;
    private SwipeRefreshLayout swipeRefresh;
    private SwipyRefreshLayout srlRefresh;
    private GateHistoryRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_door, container, false);

        llNoneDoor = view.findViewById(R.id.llNoneDoor);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        srlRefresh = view.findViewById(R.id.srlRefresh);
        srlRefresh.setOnRefreshListener(this);
        final RecyclerView rvDoorHistory = view.findViewById(R.id.rvDoorHistory);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        rvDoorHistory.setLayoutManager(layoutManager);

        adapter = new GateHistoryRecyclerAdapter(this);
        rvDoorHistory.setAdapter(adapter);


        final Bundle arguments = getArguments();
        if (arguments != null && arguments.getBoolean("FIRST", false) && !isViewShown) {
            adapter.clear();
            loadGateLogs(1);
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null) {
            isViewShown = true;
            adapter.clear();
            loadGateLogs(1);
        } else {
            isViewShown = false;
        }
    }

    @Override
    public void onGateLogClick(GateLog gateLog) {

    }

    @Override
    public void noneItemListener() {}

    private void loadGateLogs(int page) {
        if (page > TOTAL_PAGE_COUNT){
            if (srlRefresh.isRefreshing()) srlRefresh.setRefreshing(false);
            if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);
            return;
        }

        if (Config.Check()) {
            final Mobile mobile = Config.getMobile();
            ApartmentRestUtil.getIntsance(getActivity())
                    .getGateLogs(Config.getAccessToken(), mobile.getApartmentId(), page, 16)
                    .compose(bindToLifecycle())
                    .subscribe(gateLogListResponse -> {
                        if (gateLogListResponse.isSuccessful()) {
                            this.page = page + 1;

                            final GateLogList gateLogList = gateLogListResponse.body();

                            if (gateLogList != null) {
                                adapter.setItems(gateLogList.getGateLogs());
                            }

                            if (gateLogList != null) {
                                TOTAL_PAGE_COUNT = gateLogList.getPageInfo().getTotalPageCount();
                            }

                            if (srlRefresh.isRefreshing()) {
                                srlRefresh.setRefreshing(false);
                            }

                            checkItemCount();
                        } else {
                            if (srlRefresh.isRefreshing()) {
                                srlRefresh.setRefreshing(false);
                            }

                            checkItemCount();
                        }
                    },throwable -> {
                        throwable.printStackTrace();

                        checkItemCount();
                    });
        }
    }

    private void checkItemCount() {
        if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);

        if (adapter.getItemCount() < 1) {
            llNoneDoor.setVisibility(View.VISIBLE);
            srlRefresh.setVisibility(View.GONE);
        } else {
            llNoneDoor.setVisibility(View.GONE);
            srlRefresh.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        loadGateLogs(page);
    }

    @Override
    public void onRefresh() {
        TOTAL_PAGE_COUNT = 1;
        loadGateLogs(1);
    }
}
