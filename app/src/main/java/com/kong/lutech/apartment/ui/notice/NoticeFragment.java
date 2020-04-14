package com.kong.lutech.apartment.ui.notice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import com.kong.lutech.apartment.adapter.recyclerview.NoticeRecyclerAdapter;
import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.model.Notice;
import com.kong.lutech.apartment.model.list.NoticeList;
import com.kong.lutech.apartment.network.api.apartment.ApartmentRestUtil;
import com.kong.lutech.apartment.ui.MainActivity;
import com.kong.lutech.apartment.ui.notice.detail.NoticeDetailActivity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.ArrayList;

/**
 * Created by LimHoJin on 2017-01-18.
 */
public class NoticeFragment extends RxFragment implements NoticeRecyclerAdapter.OnNoticeClickListener, SwipyRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "NoticeFragment";

    private boolean isViewShown = false;

    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout llNoneNotice;

    private SwipyRefreshLayout srlRefresh;
    private NoticeRecyclerAdapter adapter;


    private int page = 1;
    private int TOTAL_PAGE_COUNT = 1;

    private ArrayList<Notice> notices;


    private BroadcastReceiver newNoticeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Mobile mobile = Config.getMobile();
            ApartmentRestUtil.getInstance(getActivity(), "yyyy.MM.dd")
                    .getNotices(Config.getAccessToken(), mobile.getApartmentId(), 1, 8)
                    .compose(bindToLifecycle())
                    .subscribe(noticeListResponse -> {
                        if (noticeListResponse.isSuccessful()) {
                            final NoticeList noticeList = noticeListResponse.body();

                            adapter.addNewItems(noticeList.getNotices());

                            checkItemCount();

                            final SharedPreferences prefs = getActivity().getSharedPreferences(Config.PREFERENCE_KEY,Context.MODE_PRIVATE);
                            final int minSequence = prefs.getInt("Notice-MinSequence", 0);
                            if (notices.size() > 0 && minSequence < noticeList.getNotices().get(0).getSequence()) {
                                prefs.edit().putInt("Notice-MinSequence", noticeList.getNotices().get(0).getSequence()).apply();
                            }
                        }
                    });
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_notice, container, false);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        llNoneNotice = view.findViewById(R.id.llNoneNotice);
        srlRefresh = view.findViewById(R.id.srlRefresh);
        srlRefresh.setOnRefreshListener(this);
        final RecyclerView listNotice = view.findViewById(R.id.listNotice);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        listNotice.setLayoutManager(layoutManager);

        adapter = new NoticeRecyclerAdapter(getActivity(), this);
        listNotice.setAdapter(adapter);

        final Bundle arguments = getArguments();
        if (arguments != null && getArguments().getBoolean("FIRST", false) && !isViewShown) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MainActivity.INTENT_NEW_NOTICE);
            getActivity().registerReceiver(newNoticeReceiver, intentFilter);
            isRegisteredReceiver = true;

            adapter.clear();
            loadNotices(1);
        }

        return view;
    }

    private boolean isRegisteredReceiver = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null) {
            isViewShown = true;

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MainActivity.INTENT_NEW_NOTICE);
            getActivity().registerReceiver(newNoticeReceiver, intentFilter);
            isRegisteredReceiver = true;

            adapter.clear();
            loadNotices(1);
        } else {
            isViewShown = false;
        }

        if (!isVisibleToUser && isRegisteredReceiver) {
            getActivity().unregisterReceiver(newNoticeReceiver);
            isRegisteredReceiver = false;
        }
    }

    @Override
    public void onNoticeClickListener(int position, Notice notice) {
        startActivity(
                new Intent(getActivity(), NoticeDetailActivity.class)
                .putExtra("Notice", notice)
        );
    }

    private void loadNotices(int page) {
        if (page > TOTAL_PAGE_COUNT) {
            if (srlRefresh.isRefreshing()) srlRefresh.setRefreshing(false);
            return;
        }

        if (Config.Check()) {
            final Mobile mobile = Config.getMobile();
            final int PAGE_OF_SIZE = 15;
            ApartmentRestUtil.getInstance(getActivity(), "yyyy.MM.dd")
                    .getNotices(Config.getAccessToken(), mobile.getApartmentId(), page, PAGE_OF_SIZE)
                    .compose(bindToLifecycle())
                    .subscribe(noticeListResponse -> {
                        if (noticeListResponse.isSuccessful()) {
                            this.page = page + 1;
                            final NoticeList noticeList = noticeListResponse.body();
                            notices = noticeList.getNotices();

                            adapter.addItems(notices);
                            TOTAL_PAGE_COUNT = noticeList.getPageInfo().getTotalPageCount();

                            if (srlRefresh.isRefreshing()) srlRefresh.setRefreshing(false);

                            checkItemCount();


                            final SharedPreferences prefs = getActivity().getSharedPreferences(Config.PREFERENCE_KEY,Context.MODE_PRIVATE);
                            final int minSequence = prefs.getInt("Notice-MinSequence", 0);
                            if (notices.size() > 0 && minSequence < notices.get(0).getSequence()) {
                                prefs.edit().putInt("Notice-MinSequence", notices.get(0).getSequence()).apply();
                            }
                        } else {
                            if (srlRefresh.isRefreshing()) srlRefresh.setRefreshing(false);

                            checkItemCount();
                        }
                    }, throwable -> {
                        throwable.printStackTrace();

                        if (srlRefresh.isRefreshing()) srlRefresh.setRefreshing(false);
                        checkItemCount();
                    });
        }
    }

    private void checkItemCount() {
        if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);

        if (adapter.getItemCount() < 1 ) {
            llNoneNotice.setVisibility(View.VISIBLE);
            srlRefresh.setVisibility(View.GONE);
        } else {
            llNoneNotice.setVisibility(View.GONE);
            srlRefresh.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.refresh();
        getActivity().sendBroadcast(new Intent(MainActivity.INTENT_TAB_NOTICE));
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        loadNotices(page);
    }

    @Override
    public void onRefresh() {
        page = 1;
        TOTAL_PAGE_COUNT = 1;

        adapter.clear();
        loadNotices(page);
    }
}
