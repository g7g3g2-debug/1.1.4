package com.kong.lutech.apartment.ui.delivery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kong.lutech.apartment.Config;
import com.kong.lutech.apartment.adapter.recyclerview.DeliveryAlarmRecyclerAdapter;
import com.kong.lutech.apartment.model.Delivery;
import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.model.list.DeliveryList;
import com.kong.lutech.apartment.network.api.apartment.ApartmentRestUtil;
import com.kong.lutech.apartment.ui.MainActivity;
import com.kong.lutech.apartment.utils.sqlite.DBManager;
import com.kongtech.lutech.apartment.R;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.List;

public class DeliveryFragment extends RxFragment implements DeliveryAlarmRecyclerAdapter.OnDeliveryClickListener, SwipyRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "DeliveryFragment";

    private boolean isViewShown = false;

    private static int PAGE_OF_SIZE = 15;
    private int page = 1;
    private int TOTAL_PAGE_COUNT = 1;


    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout llNoneDelivery;

    private SwipyRefreshLayout srlRefresh;
    private RecyclerView listDelivery;
    private DeliveryAlarmRecyclerAdapter adapter;

    private int unreadCount = 0;
    private int firstItemRange = -1;

    private BroadcastReceiver newDeliveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != MainActivity.INTENT_NEW_DELIVERY) return;

            final Mobile mobile = Config.getMobile();
            ApartmentRestUtil.getIntsance(getActivity()).getDeliveries(Config.getAccessToken(), mobile.getApartmentId(), mobile.getDongId(), mobile.getHomeId(), 1, 8)
                    .compose(bindToLifecycle())
                    .subscribe(deliveryListResponse -> {
                        if (deliveryListResponse.isSuccessful()) {
                            final DeliveryList deliveryList = deliveryListResponse.body();

                            adapter.addNewItems(deliveryList.getDeliveries());

                            checkItemCount();

                            final SharedPreferences prefs = getActivity().getSharedPreferences(Config.PREFERENCE_KEY,Context.MODE_PRIVATE);
                            final int minSequence = prefs.getInt("Delivery-MinSequence", 0);
                            if (deliveryList.getDeliveries().size() > 0 && minSequence < deliveryList.getDeliveries().get(0).getSequence()) {
                                prefs.edit().putInt("Delivery-MinSequence", deliveryList.getDeliveries().get(0).getSequence()).apply();
                            }

                            final List<Delivery> deliveries = deliveryList.getDeliveries();
                            if (deliveries.size() > 0) {
                                final DBManager dbManager = new DBManager(getActivity()).open();
                                dbManager.clearDeliveries();
                                unreadCount = dbManager.deliveriesCount();
                                dbManager.close();
                            }

                            getActivity().sendBroadcast(new Intent(MainActivity.INTENT_TAB_DELIVERY));
                        }
                    });
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_delivery, container, false);

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        llNoneDelivery = (LinearLayout) view.findViewById(R.id.llNoneDelivery);
        srlRefresh = (SwipyRefreshLayout) view.findViewById(R.id.srlRefresh);
        srlRefresh.setOnRefreshListener(this);
        listDelivery = (RecyclerView) view.findViewById(R.id.listDelivery);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        listDelivery.setLayoutManager(layoutManager);

        adapter = new DeliveryAlarmRecyclerAdapter(this);
        listDelivery.setAdapter(adapter);
        listDelivery.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (firstItemRange != layoutManager.findFirstVisibleItemPosition()) {
                    firstItemRange = layoutManager.findFirstVisibleItemPosition();

                    if (firstItemRange == (unreadCount - 1) && firstItemRange > -1) {
                        final Delivery delivery = adapter.getPositionItem(firstItemRange);

                        DBManager dbManager = new DBManager(getActivity()).open();
                        dbManager.deleteDelivery(delivery.getDeliveryId());
                        unreadCount = dbManager.deliveriesCount();
                        dbManager.close();

                        getActivity().sendBroadcast(new Intent(MainActivity.INTENT_TAB_DELIVERY));
                    }
                }
            }
        });

        final Bundle arguments = getArguments();
        if (arguments != null && getArguments().getBoolean("FIRST", false) && !isViewShown) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MainActivity.INTENT_NEW_DELIVERY);
            getActivity().registerReceiver(newDeliveryReceiver, intentFilter);
            isRegisteredReceiver = true;

            adapter.clear();
            loadDeliveries(1);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().sendBroadcast(new Intent(MainActivity.INTENT_NEW_DELIVERY));
    }

    private void loadDeliveries(int page) {
        if (page > TOTAL_PAGE_COUNT){
            if (srlRefresh.isRefreshing()) srlRefresh.setRefreshing(false);
            return;
        }
        if (Config.Check()) {
            final Mobile mobile = Config.getMobile();
            ApartmentRestUtil.getIntsance(getActivity())
                    .getDeliveries(Config.getAccessToken(), mobile.getApartmentId(), mobile.getDongId(), mobile.getHomeId(), page, PAGE_OF_SIZE)
                    .compose(bindToLifecycle())
                    .subscribe(deliveryListResponse -> {
                        if (deliveryListResponse.isSuccessful()) {
                            this.page = page + 1;
                            final DeliveryList deliveryList = deliveryListResponse.body();

                            adapter.addItems(deliveryList.getDeliveries());

                            TOTAL_PAGE_COUNT = deliveryList.getPageInfo().getTotalPageCount();

                            if (srlRefresh.isRefreshing()) {
                                srlRefresh.setRefreshing(false);
                            }

                            checkItemCount();


                            SharedPreferences prefs = getActivity().getSharedPreferences(Config.PREFERENCE_KEY,Context.MODE_PRIVATE);
                            final int minSequence = prefs.getInt("Delivery-MinSequence", 0);
                            if (deliveryList.getDeliveries().size() > 0 && minSequence < deliveryList.getDeliveries().get(0).getSequence()) {
                                prefs.edit().putInt("Delivery-MinSequence", deliveryList.getDeliveries().get(0).getSequence()).apply();
                            }
                        } else {
                            if (srlRefresh.isRefreshing()) {
                                srlRefresh.setRefreshing(false);
                            }

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

        if (getView() != null) {
            isViewShown = true;

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MainActivity.INTENT_NEW_DELIVERY);
            getActivity().registerReceiver(newDeliveryReceiver, intentFilter);
            isRegisteredReceiver = true;

            adapter.clear();
            loadDeliveries(1);
        } else {
            isViewShown = false;
        }

        if (!isVisibleToUser && isRegisteredReceiver) {
            getActivity().unregisterReceiver(newDeliveryReceiver);
            isRegisteredReceiver = false;
        }
    }

    private void checkItemCount() {
        if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);

        if (adapter.getItemCount() < 1) {
            llNoneDelivery.setVisibility(View.VISIBLE);
            srlRefresh.setVisibility(View.GONE);
        } else {
            llNoneDelivery.setVisibility(View.GONE);
            srlRefresh.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDeliveryClick(Delivery delivery) {

    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        loadDeliveries(page);
    }

    @Override
    public void onRefresh() {
        page = 1;
        TOTAL_PAGE_COUNT = 1;

        adapter.clear();
        loadDeliveries(page);
    }
}
