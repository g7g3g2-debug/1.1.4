package com.kong.lutech.apartment.adapter.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kong.lutech.apartment.ui.door.DoorFragment;
import com.kong.lutech.apartment.ui.main.MainFragment;
import com.kong.lutech.apartment.ui.notice.NoticeFragment;
import com.kong.lutech.apartment.ui.parking.ParkingFragment;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.model.Permission;
import com.kong.lutech.apartment.ui.delivery.DeliveryFragment;
import com.kong.lutech.apartment.ui.setting.SettingFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gimdonghyeog on 2017. 8. 11..
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public class FragmentItem {
        private Fragment fragment;
        private String title;
        private int icon;

        public FragmentItem(Fragment fragment, String title, int icon) {
            this.fragment = fragment;
            this.title = title;
            this.icon = icon;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }
    }

    private List<FragmentItem> fragmentItems;

    public List<FragmentItem> getFragmentItems() {
        return fragmentItems;
    }

    public SectionsPagerAdapter(final FragmentManager fm, final Permission permission, final int aptId) {
        super(fm);
        fragmentItems = new ArrayList<>();

        if (permission == null) {
            fragmentItems.add(new FragmentItem(isUsePreferenceParking(aptId) ? new ParkingFragment() : new MainFragment(), "주차위치 저장", R.drawable.tab_ic_park_selector));
            fragmentItems.add(new FragmentItem(new DoorFragment(), "공동현관 출입기록", R.drawable.tab_ic_door_selector));
            fragmentItems.add(new FragmentItem(new DeliveryFragment(), "택배알림", R.drawable.tab_ic_delivery_selector));
            fragmentItems.add(new FragmentItem(new NoticeFragment(), "공지사항", R.drawable.tab_ic_notice_selector));
        } else {
            if (permission.isParkingInfo()) {
                fragmentItems.add(new FragmentItem(isUsePreferenceParking(aptId) ? new ParkingFragment() : new MainFragment(), "주차위치 저장", R.drawable.tab_ic_park_selector));
            }
            if (permission.isGateLog()) {
                fragmentItems.add(new FragmentItem(new DoorFragment(), "공동현관 출입기록", R.drawable.tab_ic_door_selector));
            }
            if (permission.isDelivery()) {
                fragmentItems.add(new FragmentItem(new DeliveryFragment(), "택배알림", R.drawable.tab_ic_delivery_selector));
            }
            if (permission.isNotice()) {
                fragmentItems.add(new FragmentItem(new NoticeFragment(), "공지사항", R.drawable.tab_ic_notice_selector));
            }
        }

        fragmentItems.add(new FragmentItem(new SettingFragment(), "설정", R.drawable.tab_ic_setting_selector));

        Bundle bundle = new Bundle();
        bundle.putBoolean("FIRST", true);
        fragmentItems.get(0).getFragment().setArguments(bundle);
    }

    private boolean isUsePreferenceParking(final int aptId) {
        return !(aptId == 260 || aptId == 280);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentItems.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return fragmentItems.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentItems.get(position).getTitle();
    }
}
