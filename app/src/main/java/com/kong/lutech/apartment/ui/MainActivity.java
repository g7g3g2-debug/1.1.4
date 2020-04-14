package com.kong.lutech.apartment.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.kong.lutech.apartment.ui.notice.NoticeFragment;
import com.kong.lutech.apartment.Config;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.adapter.viewpager.SectionsPagerAdapter;
import com.kong.lutech.apartment.model.Delivery;
import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.model.Notice;
import com.kong.lutech.apartment.model.list.DeliveryList;
import com.kong.lutech.apartment.model.list.NoticeList;
import com.kong.lutech.apartment.network.api.apartment.ApartmentRestUtil;
import com.kong.lutech.apartment.ui.delivery.DeliveryFragment;
import com.kong.lutech.apartment.utils.sqlite.DBManager;
import com.kong.lutech.apartment.view.NotificationTab;
import com.kongtech.smapsdk.services.PersistentService;
import com.kongtech.smapsdk.utils.HexUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import java.util.List;

public class MainActivity extends RxAppCompatActivity {
    public static final String TAG = "MainActivity";
    public static final int backPressureExitDelay = 2000;

    public static final String INTENT_NEW_PARKING = "INTENT_NEW_PARKING";
    public static final String INTENT_NEW_DELIVERY = "INTENT_NEW_DELIVERY";
    public static final String INTENT_NEW_NOTICE = "INTENT_NEW_NOTICE";

    public static final String INTENT_TAB_DELIVERY = "INTENT_TAB_DELIVERY";
    public static final String INTENT_TAB_NOTICE = "INTENT_TAB_NOTICE";


    private ViewPager mViewPager;
    private NotificationTab tabDelivery, tabNotice;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    // Debug View
    private TextView textView;


    private BroadcastReceiver notiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case INTENT_NEW_DELIVERY:
                    refreshDeliveryTab();
                    break;
                case INTENT_NEW_NOTICE:
                    refreshNoticeTab();
                    break;
                case INTENT_TAB_DELIVERY:
                    refreshDeliveryTab();
                    break;
                case INTENT_TAB_NOTICE:
                    refreshNoticeTab();
                    break;


                case "TEST":
                    textView.append(intent.getStringExtra("RSSI") + "\n");
                    break;
            }
        }
    };

    private void refreshNoticeTab() {
        final DBManager dbManager = new DBManager(getApplicationContext()).open();
        tabNotice.setCount(dbManager.noticesCount());
        dbManager.close();
    }

    private void refreshDeliveryTab() {
        final DBManager dbManager1 = new DBManager(getApplicationContext()).open();
        tabDelivery.setCount(dbManager1.deliveriesCount());
        dbManager1.close();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {
        Config.CheckingUser(this);

        setupViews();
        setupTapNoti();

        setupNotiReceiver();

        setupPersistentService();


        final Mobile mobile = Config.getMobile();
        final SharedPreferences prefs = getSharedPreferences(Config.PREFERENCE_KEY, MODE_PRIVATE);

        requestCheckNewNotices(mobile, prefs);
        requestCheckNewDeliveries(mobile, prefs);

        if (Config.Check()) {
            final String authCode = Config.getAuthCode();
            byte[] codeArr = HexUtils.hexStringToBytes(authCode.substring(1, authCode.length()));
            byte[] array = new byte[codeArr.length + 1];
            array[0] = (byte)authCode.charAt(0);

            for(int i = 0; i < codeArr.length; ++i) {
                array[1 + i] = codeArr[i];
            }
            Log.d(TAG, "AuthCode to Hex : " + HexUtils.bytesToHexString(array));
        }
    }

    private void setupTapNoti() {
        DBManager dbManager = new DBManager(getApplicationContext()).open();
        if (tabDelivery != null) {
            tabDelivery.setCount(dbManager.deliveriesCount());
        }
        if (tabNotice != null) {
            tabNotice.setCount(dbManager.noticesCount());
        }
        dbManager.close();
    }

    private void setupNotiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_NEW_DELIVERY);
        intentFilter.addAction(INTENT_NEW_NOTICE);
        intentFilter.addAction(INTENT_TAB_DELIVERY);
        intentFilter.addAction(INTENT_TAB_NOTICE);
        intentFilter.addAction("TEST");
        registerReceiver(notiReceiver, intentFilter);
    }

    private void setupViews() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolBarTitleStyle);


        findViewById(R.id.btnClear).setOnClickListener((v) -> {
            textView.setText("");
            sendBroadcast(new Intent("CLEAR"));
        });
        textView = findViewById(R.id.textView);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), Config.getPermission(), Config.getMobile().getApartmentId());

        mViewPager = findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        initTab(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                setTitle(mSectionsPagerAdapter.getPageTitle(position));

                if (mSectionsPagerAdapter.getItem(position) instanceof DeliveryFragment) {
                    final DBManager dbManager = new DBManager(getApplicationContext()).open();
                    dbManager.clearDeliveries();
                    if (tabDelivery != null) {
                        tabDelivery.setCount(dbManager.deliveriesCount());
                    }
                    dbManager.close();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        setTitle(mSectionsPagerAdapter.getPageTitle(mViewPager.getCurrentItem()));
    }

    private void setupPersistentService() {
        final Intent persistentService = new Intent(getApplicationContext(), PersistentService.class);
        persistentService.putExtra(PersistentService.INTENT_SETTING_PERSISTENT,
                new PersistentService.PersistentSetting(MainActivity.class.getName(), R.mipmap.ic_launcher, PushActivity.class.getName()));

        persistentService.putExtra(PersistentService.INTENT_SETTING_THRESHOLD, Config.getThreshold(getApplicationContext()));
        persistentService.putExtra(PersistentService.INTENT_SETTING_DISCOVERRANGE, Config.getDiscoverRange(getApplicationContext()));
        persistentService.putExtra(PersistentService.INTENT_SETTING_RATIOVALUE, Config.getRatioValue());

        persistentService.putExtra(PersistentService.INTENT_SETTING_VIBRATE,
                getSharedPreferences(Config.PREFERENCE_KEY, Context.MODE_PRIVATE)
                        .getBoolean("pushVibrate", true));

        persistentService.putExtra(PersistentService.INTENT_SETTING_AUTHCODE, Config.getAuthCode());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(persistentService);
        } else {
            startService(persistentService);
        }
    }

    private void requestCheckNewDeliveries(Mobile mobile, SharedPreferences prefs) {
        final int minSequenceDelivery = prefs.getInt("Delivery-MinSequence", 0);
        if (minSequenceDelivery > 0) {
            ApartmentRestUtil.getIntsance(getApplicationContext())
                    .getDeliveriesMinSequence(Config.getAccessToken(), mobile.getApartmentId(), mobile.getDongId(), mobile.getHomeId(), minSequenceDelivery + 1)
                    .compose(bindToLifecycle())
                    .subscribe(noticeListResponse -> {
                        final DeliveryList deliveryList = noticeListResponse.body();
                        if (deliveryList == null || deliveryList.getDeliveries() == null) return;

                        final List<Delivery> deliveries = deliveryList.getDeliveries();

                        if (deliveries.size() > 0) {
                            prefs.edit().putInt("Delivery-MinSequence", deliveries.get(0).getSequence()).apply();
                        }

                        final DBManager dbManager1 = new DBManager(getApplicationContext()).open();
                        boolean isRefresh = false;
                        for (Delivery notice: deliveries) {
                            if(!dbManager1.isExistDelivery(notice.getDeliveryId())) {
                                dbManager1.insertDelivery(notice.getDeliveryId());
                                isRefresh = true;
                            }
                        }
                        dbManager1.close();

                        if (isRefresh) {
                            refreshDeliveryTab();
                        }
                    }, Throwable::printStackTrace);
        }
    }

    private void requestCheckNewNotices(Mobile mobile, SharedPreferences prefs) {
        final int minSequence = prefs.getInt("Notice-MinSequence", 0);
        if (minSequence > 0) {
            ApartmentRestUtil.getInstance(getApplicationContext(), "yyyy.MM.dd")
                    .getNoticesMinSequence(Config.getAccessToken(), mobile.getApartmentId(), minSequence + 1)
                    .compose(bindToLifecycle())
                    .subscribe(noticeListResponse -> {
                        final NoticeList noticeList = noticeListResponse.body();
                        if (noticeList == null || noticeList.getNotices() == null) return;

                        final List<Notice> notices = noticeList.getNotices();

                        if (notices.size() > 0) {
                            prefs.edit().putInt("Notice-MinSequence", notices.get(0).getSequence()).apply();
                        }

                        final DBManager dbManager1 = new DBManager(getApplicationContext()).open();
                        boolean isRefresh = false;
                        for (Notice notice: notices) {
                            if(!dbManager1.isExistNotice(notice.getNoticeId())) {
                                dbManager1.insertNotice(notice.getNoticeId());
                                isRefresh = true;
                            }
                        }
                        dbManager1.close();

                        if (isRefresh) {
                            refreshNoticeTab();
                        }
                    }, Throwable::printStackTrace);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        final int index = getIntent().getIntExtra(PushActivity.EXTRA_INDEX_KEY, -1);
        if(index > -1 ) mViewPager.setCurrentItem(index);
    }

    private boolean doubleBackPressExit = false;

    @Override
    public void onBackPressed() {
        if (!doubleBackPressExit) {
            doubleBackPressExit = true;

            Toast.makeText(getApplicationContext(), "뒤로가기 키를 한번 더 클릭하면\n앱이 종료됩니다.",Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackPressExit = false, backPressureExitDelay);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(notiReceiver);
        super.onDestroy();
    }

    private void initTab(ViewPager viewPager) {
        final List<SectionsPagerAdapter.FragmentItem> fragmentItems = mSectionsPagerAdapter.getFragmentItems();

        final TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        final NotificationTab[] tab = new NotificationTab[tabLayout.getTabCount()];

        for(int i = 0 ;  i < tab.length;i++) {
            tab[i] = new NotificationTab(getApplicationContext());

            final Fragment fragment = mSectionsPagerAdapter.getItem(i);

            final int icon = fragmentItems.get(i).getIcon();
            if (fragment instanceof DeliveryFragment) {
                tab[i].setData(icon, i);
                tabDelivery = tab[i];
                tabLayout.getTabAt(i).setCustomView(tabDelivery);
            }
            else if (fragment instanceof NoticeFragment) {
                tab[i].setData(icon, i);
                tabNotice = tab[i];
                tabLayout.getTabAt(i).setCustomView(tabNotice);
            }
            else {
                tab[i].setData(icon, 0);
                tabLayout.getTabAt(i).setCustomView(tab[i]);
            }
        }
    }
}
