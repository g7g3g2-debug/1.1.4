package com.kong.lutech.apartment.ui.parking.detail;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;

import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.model.Car;
import com.kong.lutech.apartment.ui.BaseActivity;


public class ParkingDetailActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();

    private Car car;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_detail);
        setSupportActionBar(findViewById(R.id.toolbar));
        homeButtonEnabled();

        car = getIntent().getParcelableExtra("Car");
        final boolean isUsePerference = getIntent().getBooleanExtra("UsePreference", false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), isUsePerference);

        final ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] fragments;
        private final String[] pageTitles = new String[]{"주차 정보", "주차 기록"};

        SectionsPagerAdapter(FragmentManager fm, boolean isUsePreference) {
            super(fm);
            fragments = new Fragment[]{
                    (isUsePreference ? new ParkingInfoFragment() : new MainParkingInfoFragment()),
                    new ParkingHistoryFragment()};
        }

        @Override
        public Fragment getItem(int position) {
            final Fragment fragment = fragments[position];
            final Bundle bundle = new Bundle();
            bundle.putParcelable("Car", car);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles[position];
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
